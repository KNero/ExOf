package team.balam.exof.module.service;

import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.ExternalClassLoader;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.environment.vo.ServiceVariable;
import team.balam.exof.module.Module;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.Shutdown;
import team.balam.exof.module.service.annotation.Startup;
import team.balam.exof.module.service.annotation.Variable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ServiceProvider implements Module, Observer
{
	private static Logger logger = LoggerFactory.getLogger(ServiceProvider.class);
	
	private Map<String, ServiceDirectory> serviceDirectory;
	private boolean isReloadServiceVariable;
	private transient boolean isLoadingClass;

	private static ServiceProvider self = new ServiceProvider();
	
	private ServiceProvider() 
	{
		
	}
	
	public static ServiceProvider getInstance()
	{
		return self;
	}

    @SuppressWarnings("unchecked")
	private void _register(ServiceDirectoryInfo _info) throws Exception {
		Class<?> clazz = ExternalClassLoader.loadClass(_info.getClassName());
		team.balam.exof.module.service.annotation.ServiceDirectory serviceDirAnn =
				clazz.getAnnotation(team.balam.exof.module.service.annotation.ServiceDirectory.class);

		if (serviceDirAnn != null) {
			Object host = clazz.newInstance();

			this._setServiceVariableByAnnotation(host, _info);

			ServiceDirectory serviceDir = this.serviceDirectory.computeIfAbsent(_info.getPath(),
					_key -> new ServiceDirectory(host, _key));

			Set<Method> services = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Service.class));
			for (Method m : services) {
				String serviceName = this._getServiceName(m);

				serviceDir.register(serviceName, host, m);

				if (logger.isInfoEnabled()) {
					logger.info("Service is loaded. path[{}] class[{}] name[{}]", _info.getPath() + "/" + serviceName, _info.getClassName(), serviceName);
				}
			}

			Set<Method> startup = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Startup.class));
			if (!startup.isEmpty()) {
				serviceDir.setStartup(startup.iterator().next());
			}

			Set<Method> shutdown = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Shutdown.class));
			if (!shutdown.isEmpty()) {
				serviceDir.setShutdown(shutdown.iterator().next());
			}
		} else {
			ServiceInfoDao.deleteServiceDirectory(_info.getPath());
			throw new Exception("This class undefined ServiceDirectory annotation.");
		}
	}

	private String _getServiceName(Method _method) {
		String serviceName = _method.getName();
		Service serviceAnnotation = _method.getAnnotation(Service.class);

		if (!serviceAnnotation.value().isEmpty()) {
			serviceName = serviceAnnotation.value();
		}

		if (!serviceAnnotation.name().isEmpty()) {
			serviceName = serviceAnnotation.name();
		}

		return serviceName;
	}

	@SuppressWarnings("unchecked")
	private void _setServiceVariableByAnnotation(Object _host, ServiceDirectoryInfo _info) throws Exception {
		Set<Field> fields = ReflectionUtils.getAllFields(_host.getClass(), ReflectionUtils.withAnnotation(Variable.class));
		
		for(Field field : fields) {
			field.setAccessible(true);
			
			Variable variableAnn = field.getAnnotation(Variable.class);
			if(variableAnn != null) {
				ServiceVariable serviceVariables = _info.getVariable(variableAnn.value());
				if (serviceVariables == null) {
					throw new NullPointerException("Service name not found. " + variableAnn.value());
				}

				String value = serviceVariables.getString(field.getName());
				Class<?> fieldType = field.getType();

				if ("int".equals(fieldType.getName()) || fieldType.equals(Integer.class)) {
					field.set(_host, Integer.valueOf(value));
				} else if ("long".equals(fieldType.getName()) || fieldType.equals(Long.class)) {
					field.set(_host, Long.valueOf(value));
				} else if ("float".equals(fieldType.getName()) || fieldType.equals(Float.class)) {
					field.set(_host, Float.valueOf(value));
				} else if ("double".equals(fieldType.getName()) || fieldType.equals(Double.class)) {
					field.set(_host, Double.valueOf(value));
				} else if ("byte".equals(fieldType.getName()) || fieldType.equals(Byte.class)) {
					field.set(_host, Byte.valueOf(value));
				} else if ("short".equals(fieldType.getName()) || fieldType.equals(Short.class)) {
					field.set(_host, Short.valueOf(value));
				} else if (fieldType.equals(String.class)) {
					field.set(_host, value);
				} else if (fieldType.equals(List.class)) {
					field.set(_host, serviceVariables.get(field.getName()));
				} else {
					logger.error("This type can not be set. Field type : {}", fieldType);
				}
			}
		}
	}

	public static ServiceWrapper lookup(String _path) throws ServiceNotFoundException {
		if (_path == null || _path.length() == 0) {
			throw new IllegalArgumentException("Path is null : " + _path);
		}

		Map<String, ServiceDirectory> serviceDirectoryMap = self.serviceDirectory;

		self._waitLoadingService();

		int splitIdx = _path.lastIndexOf("/");
		if (splitIdx == -1) {
			throw new ServiceNotFoundException(_path);
		}

		String dirPath = _path.substring(0, splitIdx);
		String serviceName = _path.substring(splitIdx + 1);

		ServiceDirectory serviceDir = serviceDirectoryMap.get(dirPath);
		if (serviceDir == null) {
			throw new ServiceNotFoundException(_path);
		}

		ServiceWrapper service = serviceDir.getService(serviceName);
		if (service == null) {
			throw new ServiceNotFoundException(_path);
		}

		return service;
	}

	/**
	 * 현재 서비스 클래스가 로딩 중이라면 완료될 때 까지 기다린다.
	 * 만약 로딩이 1분 이상 지연된다면 에러로그를 출력한다.
	 */
	private void _waitLoadingService() {
		if (self.isLoadingClass) {
			long start = System.currentTimeMillis();
			long sleepTime = 100;

			do {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// ignore exception
				}

				long elapsed = System.currentTimeMillis() - start;
				if (elapsed > 60000) {
					sleepTime = 1000;
					logger.error("Loading service class is lazy. {} ms", elapsed);
				}
			} while (self.isLoadingClass);
		}
	}

	public void loadServiceDirectory() {
		this.isLoadingClass = true;
		this.serviceDirectory = new HashMap<>();

		try {
			this.stop();

			List<ServiceDirectoryInfo> directoryInfoList = ServiceInfoDao.selectServiceDirectory();
			directoryInfoList.forEach(_info -> {
				try {
					this._register(_info);

					logger.warn("Service Directory is loaded.\n{}", _info.toString());
				} catch(Exception e) {
					logger.error("Can not register the service. Class : {}", _info.getClassName(), e);
				}
			});
		} catch (Exception e) {
			logger.error("Fail to stop ServiceDirectory.", e);
		} finally {
			this.isLoadingClass = false;
		}
	}

	@Override
	public void start() {
		Boolean isAutoReload = SystemSetting.getFramework(EnvKey.Framework.AUTORELOAD_SERVICE_VARIABLE);
		if (isAutoReload != null) {
			this.isReloadServiceVariable = isAutoReload;
		}

		this.loadServiceDirectory();
		
		this.serviceDirectory.values().forEach(ServiceDirectory::startup);
	}

	@Override
	public void stop() {
	    if (this.serviceDirectory != null) {
            this.serviceDirectory.values().forEach(ServiceDirectory::shutdown);
        }
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!this.isReloadServiceVariable) {
			return;
		}

		String serviceDirPath = ((String[]) arg)[0];
		String serviceName = ((String[]) arg)[1];

		ServiceDirectoryInfo directoryInfo = ServiceInfoDao.selectServiceDirectory(serviceDirPath);
		if (directoryInfo.isNotNull()) {
			try {
				ServiceWrapperImpl service = (ServiceWrapperImpl) lookup(serviceDirPath + "/" + serviceName);
				this._setServiceVariableByAnnotation(service.getHost(), directoryInfo);
			} catch (Exception e) {
				logger.error("Can not reload the ServiceVariable. {}", serviceDirPath + "/" + serviceName, e);
			}
		}
	}
}
