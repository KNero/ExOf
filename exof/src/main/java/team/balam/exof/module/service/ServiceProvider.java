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
import team.balam.exof.module.service.annotation.Shutdown;
import team.balam.exof.module.service.annotation.Startup;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.Outbound;

public class ServiceProvider implements Module, Observer
{
	private static Logger logger = LoggerFactory.getLogger(ServiceProvider.class);
	
	private Map<String, ServiceDirectory> serviceDirectory = new ConcurrentHashMap<>();
	private boolean isAutoReload;
	private long updateVariableTime;
	
	private static ServiceProvider self = new ServiceProvider();
	
	private ServiceProvider() 
	{
		
	}
	
	public static ServiceProvider getInstance()
	{
		return self;
	}
	
	synchronized public static void register(ServiceDirectoryInfo _info) throws Exception
	{
		Class<?> clazz = Class.forName(_info.getClassName());
		team.balam.exof.module.service.annotation.ServiceDirectory serviceDirAnn = 
				clazz.getAnnotation(team.balam.exof.module.service.annotation.ServiceDirectory.class);
		
		if(serviceDirAnn != null)
		{
			Object host = clazz.newInstance();
			
			_setServiceVariableByAnnotation(host, _info);
			
			ServiceDirectory serdir = self.serviceDirectory.get(_info.getPath());
			if(serdir == null)
			{
				serdir = new ServiceDirectory(host, _info.getPath());
				
				self.serviceDirectory.put(_info.getPath(), serdir);
			}
			
			Method[] method = clazz.getMethods();
			for(Method m : method)
			{
				team.balam.exof.module.service.annotation.Service serviceAnn = 
						m.getAnnotation(team.balam.exof.module.service.annotation.Service.class);
				
				if(serviceAnn != null)
				{
					String serviceName = serviceAnn.name();
					if(serviceName.length() == 0) serviceName = m.getName();
					
					ServiceImpl service = serdir.register(serviceName, host, m, _info.getVariable(serviceName));
					
					_checkInboundAnnotation(m, service);
					
					_checkOutboundAnnotation(m, service);
					
					_checkMapToVoAnnotation(m, service);
					
					if(logger.isInfoEnabled())
					{
						logger.info("Service is loaded. path[{}] class[{}] name[{}]", _info.getPath() + "/" + serviceName, _info.getClassName(), serviceName);
					}
				}
				
				Startup startupAnn = m.getAnnotation(Startup.class);
				if(startupAnn != null)
				{
					serdir.setStartup(m);
				}
				
				Shutdown shutdown = m.getAnnotation(Shutdown.class);
				if(shutdown != null)
				{
					serdir.setShutdown(m);
				}
			}
		}
	}
	
	private static void _setServiceVariableByAnnotation(Object _host, ServiceDirectoryInfo _info) throws Exception
	{
		Field[] fields = _host.getClass().getDeclaredFields();
		
		for(Field field : fields)
		{
			field.setAccessible(true);
			
			Variable variableAnn = field.getAnnotation(Variable.class);
			if(variableAnn != null)
			{
				Map<String, String> serviceVariables = _info.getVariable(variableAnn.serviceName());
				String value = serviceVariables.get(field.getName());
				Class<?> fieldType = field.getType();

				if("int".equals(fieldType.toGenericString()) || fieldType.equals(Integer.class))
				{
					field.set(_host, Integer.valueOf(value));
				}
				else if("long".equals(fieldType.toGenericString()) || fieldType.equals(Long.class))
				{
					field.set(_host, Long.valueOf(value));
				}
				else if("float".equals(fieldType.toGenericString()) || fieldType.equals(Float.class))
				{
					field.set(_host, Float.valueOf(value));
				}
				else if("double".equals(fieldType.toGenericString()) || fieldType.equals(Double.class))
				{
					field.set(_host, Double.valueOf(value));
				}
				else if("byte".equals(fieldType.toGenericString()) || fieldType.equals(Byte.class))
				{
					field.set(_host, Byte.valueOf(value));
				}
				else if("short".equals(fieldType.toGenericString()) || fieldType.equals(Short.class))
				{
					field.set(_host, Short.valueOf(value));
				}
				else
				{
					field.set(_host, value);
				}
			}
		}
	}
	
	private static void _checkInboundAnnotation(Method _method, ServiceImpl _service) throws Exception
	{
		team.balam.exof.module.service.annotation.Inbound inboundAnn = 
				_method.getAnnotation(team.balam.exof.module.service.annotation.Inbound.class);
		
		if(inboundAnn != null && inboundAnn.className().trim().length() > 0)
		{
			String[] inList = inboundAnn.className().split(",");
			for(String inClass : inList)
			{
				if(inClass.trim().length() > 0)
				{
					_service.addInbound((Inbound)Class.forName(inClass.trim()).newInstance());
				}
			}
		}
	}
	
	private static void _checkOutboundAnnotation(Method _method, ServiceImpl _service) throws Exception
	{
		team.balam.exof.module.service.annotation.Outbound outboundAnn = 
				_method.getAnnotation(team.balam.exof.module.service.annotation.Outbound.class);
		
		if(outboundAnn != null && outboundAnn.className().trim().length() > 0)
		{
			String[] outList = outboundAnn.className().split(",");
			for(String outClass : outList)
			{
				if(outClass.trim().length() > 0)
				{
					_service.addOutbound((Outbound<?, ?>)Class.forName(outClass.trim()).newInstance());
				}
			}
		}
	}
	
	private static void _checkMapToVoAnnotation(Method _method, ServiceImpl _service) throws Exception
	{
		team.balam.exof.module.service.annotation.MapToVo mapTovoAnn = 
				_method.getAnnotation(team.balam.exof.module.service.annotation.MapToVo.class);
		
		if(mapTovoAnn != null && mapTovoAnn.className().trim().length() > 0)
		{
			_service.setMapToVoConverter(mapTovoAnn.className());
		}
	}
	
	public static Service lookup(String _path) throws Exception
	{
		if(_path == null || _path.length() == 0) throw new IllegalArgumentException("Path is null : " + _path);
			
		int splitIdx = _path.lastIndexOf("/");
		
		if(splitIdx == -1) throw new IllegalArgumentException("Invalid path : " + _path);
		
		String dirPath = _path.substring(0, splitIdx);
		String serviceName = _path.substring(splitIdx + 1);
		
		ServiceDirectory serviceDir = self.serviceDirectory.get(dirPath);
		if(serviceDir == null) throw new ServiceNotFoundException(_path);
		
		Service service = serviceDir.getService(serviceName);
		if(service == null) throw new ServiceNotFoundException(_path);
		
		return service;
	}

	@Override
	public void start() throws Exception
	{
		this.isAutoReload = (Boolean)SystemSetting.getInstance().
				get(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.AUTORELOAD_SERVICE_VARIABLE);
		
		List<ServiceDirectoryInfo> directoryInfoList = SystemSetting.getInstance().getList(EnvKey.PreFix.SERVICE, EnvKey.Service.SERVICES);
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
		
		this.serviceDirectory.values().forEach(_serviceDir -> {
			_serviceDir.startup();
		});
	}

	@Override
	public void stop() throws Exception
	{
		this.serviceDirectory.values().forEach(_serviceDir -> {
			_serviceDir.shutdown();
		});
	}

	@Override
	public void update(Observable o, Object arg)
	{
		List<ServiceDirectoryInfo> directoryInfoList = SystemSetting.getInstance().getList(EnvKey.PreFix.SERVICE, EnvKey.Service.SERVICES);
		
		if(! this.isAutoReload) return;
		
		if(System.currentTimeMillis() - this.updateVariableTime > 5000)
		{
			this.updateVariableTime = System.currentTimeMillis();
			
			directoryInfoList.forEach(_info -> {
				self.updateServiceDirectory(_info);
			});
		}
	}
	
	public void updateServiceDirectory(ServiceDirectoryInfo _serviceDirInfo)
	{
		try
		{
			Class<?> clazz = Class.forName(_serviceDirInfo.getClassName());
			Method[] methods = clazz.getMethods();
			
			for(Method m : methods)
			{
				team.balam.exof.module.service.annotation.Service serviceAnn = 
						m.getAnnotation(team.balam.exof.module.service.annotation.Service.class);
				
				if(serviceAnn != null)
				{
					String serviceName = serviceAnn.name();
					if(serviceName.length() == 0) serviceName = m.getName();
					
					Map<String, String> serviceVariable = _serviceDirInfo.getVariable(serviceName);
					
					ServiceDirectory serviceDir = this.serviceDirectory.get(_serviceDirInfo.getPath());
					if(serviceDir != null)
					{
						serviceDir.reloadVariable(serviceName, serviceVariable);
						
						logger.warn("Complete reloading ServiceVariable. [{}]", _serviceDirInfo.getPath() + "/" + serviceName);
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Can not reload the ServiceVariable. Class : {}", _serviceDirInfo.getClassName());
		}
	}
	
	public Map<String, HashMap<String, Object>> getAllServiceInfo() 
	{
		Map<String, HashMap<String, Object>> serviceList = new HashMap<>();
		
		this.serviceDirectory.keySet().forEach(_dirPath -> {
			ServiceDirectory serviceDir = this.serviceDirectory.get(_dirPath);
			HashMap<String, Object> serviceMap = new HashMap<>();
			serviceList.put(_dirPath, serviceMap);
			
			serviceDir.getServiceNameList().forEach(_name -> {
				ServiceImpl service = (ServiceImpl)serviceDir.getService(_name);
				
				if(! serviceMap.containsKey(EnvKey.Service.CLASS))
				{
					serviceMap.put(EnvKey.Service.CLASS, service.getHost().getClass().getName());
				}
				
				serviceMap.put(_name, service.getMethod().getName());
				serviceMap.put(_name + EnvKey.Service.SERVICE_VARIABLE, service.getAllServiceVariable());
			});
		});
		
		return serviceList;
	}
}
