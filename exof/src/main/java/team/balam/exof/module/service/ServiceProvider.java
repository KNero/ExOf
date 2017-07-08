package team.balam.exof.module.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.Module;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.service.annotation.*;

public class ServiceProvider implements Module, Observer
{
	private static Logger logger = LoggerFactory.getLogger(ServiceProvider.class);
	
	private Map<String, ServiceDirectory> serviceDirectory = new ConcurrentHashMap<>();
	private boolean isAutoReload;

	private static ServiceProvider self = new ServiceProvider();
	
	private ServiceProvider() 
	{
		
	}
	
	public static ServiceProvider getInstance()
	{
		return self;
	}

	synchronized public static void register(ServiceDirectoryInfo _info) throws Exception {
		Class<?> clazz = Class.forName(_info.getClassName());
		team.balam.exof.module.service.annotation.ServiceDirectory serviceDirAnn =
				clazz.getAnnotation(team.balam.exof.module.service.annotation.ServiceDirectory.class);

		if (serviceDirAnn != null) {
			Object host = clazz.newInstance();

			_setServiceVariableByAnnotation(host, _info);

			ServiceDirectory serviceDir = self.serviceDirectory.computeIfAbsent(_info.getPath(),
					_key -> new ServiceDirectory(host, _key));

			Method[] method = clazz.getMethods();
			for (Method m : method) {
				team.balam.exof.module.service.annotation.Service serviceAnn =
						m.getAnnotation(team.balam.exof.module.service.annotation.Service.class);

				if (serviceAnn != null) {
					String serviceName = serviceAnn.name();
					if (serviceName.length() == 0) serviceName = m.getName();

					ServiceImpl service = serviceDir.register(serviceName, host, m, _info.getVariable(serviceName));

					_checkInboundAnnotation(m, service);
					_checkOutboundAnnotation(m, service);
					_checkMapToVoAnnotation(m, service);

					if (logger.isInfoEnabled()) {
						logger.info("Service is loaded. path[{}] class[{}] name[{}]", _info.getPath() + "/" + serviceName, _info.getClassName(), serviceName);
					}
				}

				Startup startupAnn = m.getAnnotation(Startup.class);
				if (startupAnn != null) {
					serviceDir.setStartup(m);
				}

				Shutdown shutdown = m.getAnnotation(Shutdown.class);
				if (shutdown != null) {
					serviceDir.setShutdown(m);
				}
			}
		} else {
			throw new Exception("This class is not defined ServiceDirectory annotation.");
		}
	}
	
	private static void _setServiceVariableByAnnotation(Object _host, ServiceDirectoryInfo _info) throws Exception {
		Field[] fields = _host.getClass().getDeclaredFields();
		
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

	private static void _checkInboundAnnotation(Method _method, ServiceImpl _service) throws Exception {
		Inbound inboundAnn =
				_method.getAnnotation(Inbound.class);

		if (inboundAnn != null) {
			_service.addInbound(inboundAnn.classObject().newInstance());
		}
	}

	private static void _checkOutboundAnnotation(Method _method, ServiceImpl _service) throws Exception {
		Outbound outboundAnn =
				_method.getAnnotation(Outbound.class);

		if (outboundAnn != null) {
			_service.addOutbound(outboundAnn.classObject().newInstance());
		}
	}

	private static void _checkMapToVoAnnotation(Method _method, ServiceImpl _service) throws Exception {
		MapToVo mapTovoAnn =
				_method.getAnnotation(MapToVo.class);

		if (mapTovoAnn != null) {
			_service.setMapToVoConverter(mapTovoAnn.classObject());
		}
	}

	public static Service lookup(String _path) throws ServiceNotFoundException {
		if (_path == null || _path.length() == 0) throw new IllegalArgumentException("Path is null : " + _path);

		int splitIdx = _path.lastIndexOf("/");
		if (splitIdx == -1) {
			throw new ServiceNotFoundException(_path);
		}

		String dirPath = _path.substring(0, splitIdx);
		String serviceName = _path.substring(splitIdx + 1);

		ServiceDirectory serviceDir = self.serviceDirectory.get(dirPath);
		if (serviceDir == null) {
			throw new ServiceNotFoundException(_path);
		}

		Service service = serviceDir.getService(serviceName);
		if (service == null) throw new ServiceNotFoundException(_path);

		return service;
	}

	@Override
	public void start() throws Exception
	{
		Boolean isAutoReload = SystemSetting.getInstance().getFramework(EnvKey.Framework.AUTORELOAD_SERVICE_VARIABLE);
		if (isAutoReload != null) {
			this.isAutoReload = isAutoReload;
		}

		List<ServiceDirectoryInfo> directoryInfoList = SystemSetting.getInstance().getList(EnvKey.FileName.SERVICE, EnvKey.Service.SERVICES);
		directoryInfoList.forEach(_info -> {
			try
			{
				ServiceProvider.register(_info);
				
 				logger.warn("Service Directory is loaded.\n{}", _info.toString());
			}
			catch(Exception e)
			{
				logger.error("Can not register the service. Class : {}", _info.getClassName(), e);
			}
		});
		
		this.serviceDirectory.values().forEach(ServiceDirectory::startup);
	}

	@Override
	public void stop() throws Exception
	{
		this.serviceDirectory.values().forEach(ServiceDirectory::shutdown);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!this.isAutoReload) {
			return;
		}

		List<ServiceDirectoryInfo> directoryInfoList = SystemSetting.getInstance().getList(EnvKey.FileName.SERVICE, EnvKey.Service.SERVICES);
		directoryInfoList.forEach(_info -> self._updateServiceDirectory(_info));
	}

	private void _updateServiceDirectory(ServiceDirectoryInfo _serviceDirInfo) {
		try {
			Class<?> clazz = Class.forName(_serviceDirInfo.getClassName());
			Method[] methods = clazz.getMethods();

			for (Method m : methods) {
				team.balam.exof.module.service.annotation.Service serviceAnn =
						m.getAnnotation(team.balam.exof.module.service.annotation.Service.class);

				if (serviceAnn != null) {
					String serviceName = serviceAnn.name();
					if (serviceName.length() == 0) {
						serviceName = m.getName();
					}

					ServiceDirectory serviceDir = this.serviceDirectory.get(_serviceDirInfo.getPath());
					if (serviceDir != null) {
						this._reloadServiceVariable(_serviceDirInfo.getPath(), serviceName, _serviceDirInfo);

						logger.warn("Complete reloading ServiceVariable. [{}]", _serviceDirInfo.getPath() + "/" + serviceName);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Can not reload the ServiceVariable. Class : {}", _serviceDirInfo.getClassName());
		}
	}

	private void _reloadServiceVariable(String _serviceDirPath, String _serviceName, ServiceDirectoryInfo _info) {
		try {
			ServiceImpl service = (ServiceImpl) lookup(_serviceDirPath + "/" + _serviceName);
			service.setVariable(_info.getVariable(_serviceName));

			try {
				_setServiceVariableByAnnotation(service.getHost(), _info);
			} catch (Exception e) {
				logger.error("Can not reload service's member variable.", e);
			}
		} catch (ServiceNotFoundException e) {
			logger.error("Can not reload service variable because Service is not exits.");
		}
	}

	public Map<String, HashMap<String, Object>> getAllServiceInfo() {
		Map<String, HashMap<String, Object>> serviceList = new HashMap<>();

		this.serviceDirectory.keySet().forEach(_dirPath -> {
			ServiceDirectory serviceDir = this.serviceDirectory.get(_dirPath);
			HashMap<String, Object> serviceMap = new HashMap<>();
			serviceList.put(_dirPath, serviceMap);

			serviceDir.getServiceNameList().forEach(_name -> {
				ServiceImpl service = (ServiceImpl) serviceDir.getService(_name);

				if (!serviceMap.containsKey(EnvKey.Service.CLASS)) {
					serviceMap.put(EnvKey.Service.CLASS, service.getHost().getClass().getName());
				}
				
				Map<String, Object> serviceVariableMap = this.makeServiceVariableMap(service);

				serviceMap.put(_name, service.getMethod().getName());
				serviceMap.put(_name + EnvKey.Service.SERVICE_VARIABLE, serviceVariableMap);
			});
		});

		return serviceList;
	}
	
	private Map<String, Object> makeServiceVariableMap(Service _service) {
		Map<String, Object> variables = new HashMap<>();
		
		for (String key : _service.getServiceVariableKeys()) {
			variables.put(key, _service.getServiceVariable(key));
		}
		
		return variables;
	}
}
