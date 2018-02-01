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
				String serviceName = m.getAnnotation(Service.class).name();
				if (serviceName.length() == 0) {
					serviceName = m.getName();
				}

				serviceDir.register(serviceName, host, m, _info.getVariable(serviceName));

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

	@SuppressWarnings("unchecked")
	private void _setServiceVariableByAnnotation(Object _host, ServiceDirectoryInfo _info) throws Exception {
		Set<Field> fields = ReflectionUtils.getAllFields(_host.getClass(), ReflectionUtils.withAnnotation(Variable.class));
		
		for(Field field : fields) {
			field.setAccessible(true);
			
			Variable variableAnn = field.getAnnotation(Variable.class);
			if(variableAnn != null) {
				ServiceVariable serviceVariables = _info.getVariable(variableAnn.serviceName());
				if (serviceVariables == null) {
					throw new NullPointerException("Service name not found. " + variableAnn.serviceName());
				}

				String value = serviceVariables.getString(field.getName());
				Class<?> fieldType = field.getType();

				if ("int".equals(fieldType.toGenericString()) || fieldType.equals(Integer.class)) {
					field.set(_host, Integer.valueOf(value));
				} else if ("long".equals(fieldType.toGenericString()) || fieldType.equals(Long.class)) {
					field.set(_host, Long.valueOf(value));
				} else if ("float".equals(fieldType.toGenericString()) || fieldType.equals(Float.class)) {
					field.set(_host, Float.valueOf(value));
				} else if ("double".equals(fieldType.toGenericString()) || fieldType.equals(Double.class)) {
					field.set(_host, Double.valueOf(value));
				} else if ("byte".equals(fieldType.toGenericString()) || fieldType.equals(Byte.class)) {
					field.set(_host, Byte.valueOf(value));
				} else if ("short".equals(fieldType.toGenericString()) || fieldType.equals(Short.class)) {
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

		while(self.isLoadingClass) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

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

	public void loadServiceDirectory() {
		this.isLoadingClass = true;
		this.serviceDirectory = new HashMap<>();

		try {
			this.stop();
		} catch (Exception e) {
			logger.error("Fail to stop ServiceDirectory.", e);
		}

		List<ServiceDirectoryInfo> directoryInfoList = ServiceInfoDao.selectServiceDirectory();
		directoryInfoList.forEach(_info -> {
			try {
				this._register(_info);

				logger.warn("Service Directory is loaded.\n{}", _info.toString());
			} catch(Exception e) {
				logger.error("Can not register the service. Class : {}", _info.getClassName(), e);
			}
		});

		this.isLoadingClass = false;
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
	public void stop()
	{
		this.serviceDirectory.values().forEach(ServiceDirectory::shutdown);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!this.isReloadServiceVariable) {
			return;
		}

		String serviceDirPath = ((String[]) arg)[0];
		String serviceName = ((String[]) arg)[1];

		ServiceVariable serviceVariable = ServiceInfoDao.selectServiceVariable(serviceDirPath, serviceName);
		if (!serviceVariable.isNull()) {
			try {
				ServiceWrapperImpl service = (ServiceWrapperImpl) lookup(serviceDirPath + "/" + serviceName);
				service.setVariable(serviceVariable);
			} catch (ServiceNotFoundException e) {
				logger.error("Can not reload the ServiceVariable. {}", serviceDirPath + "/" + serviceName);
			}
		}
	}
}
