package team.balam.exof.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.Module;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.service.annotation.Startup;
import team.balam.exof.service.annotation.Shutdown;
import team.balam.exof.service.component.Inbound;
import team.balam.exof.service.component.Outbound;
import team.balam.exof.util.CollectionUtil;

public class ServiceProvider implements Module, Observer
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String, ServiceDirectory> serviceDirectory = new HashMap<>();
	private boolean isAutoReload;
	
	private static ServiceProvider self = new ServiceProvider();
	
	private ServiceProvider() {}
	
	public static ServiceProvider getInstance()
	{
		return self;
	}
	
	synchronized public static void register(ServiceDirectoryInfo _info) throws Exception
	{
		Class<?> clazz = Class.forName(_info.getClassName());
		team.balam.exof.service.annotation.ServiceDirectory serviceDirAnn = 
				clazz.getAnnotation(team.balam.exof.service.annotation.ServiceDirectory.class);
		
		if(serviceDirAnn != null)
		{
			Object host = clazz.newInstance();
			
			ServiceDirectory serdir = self.serviceDirectory.get(_info.getPath());
			if(serdir == null)
			{
				serdir = new ServiceDirectory(_info.getPath());
				serdir.setHost(host);
				
				self.serviceDirectory.put(_info.getPath(), serdir);
			}
			
			Method[] method = clazz.getMethods();
			for(Method m : method)
			{
				team.balam.exof.service.annotation.Service serviceAnn = 
						m.getAnnotation(team.balam.exof.service.annotation.Service.class);
				
				if(serviceAnn != null)
				{
					String serviceName = serviceAnn.name();
					if(serviceName.length() == 0) serviceName = m.getName();
					
					ServiceImpl service = serdir.register(serviceName, host, m, _info.getVariable(serviceName));
					
					_checkInboundAnnotation(m, service);
					
					_checkOutboundAnnotation(m, service);
					
					_checkMapToVoAnnotation(m, service);
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
	
	private static void _checkInboundAnnotation(Method _method, ServiceImpl _service) throws Exception
	{
		team.balam.exof.service.annotation.Inbound inboundAnn = 
				_method.getAnnotation(team.balam.exof.service.annotation.Inbound.class);
		
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
		team.balam.exof.service.annotation.Outbound outboundAnn = 
				_method.getAnnotation(team.balam.exof.service.annotation.Outbound.class);
		
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
		team.balam.exof.service.annotation.MapToVo mapTovoAnn = 
				_method.getAnnotation(team.balam.exof.service.annotation.MapToVo.class);
		
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
		
		List<ServiceDirectoryInfo> directoryInfoList = 
				SystemSetting.getInstance().getListAndRemove(EnvKey.PreFix.SERVICE, EnvKey.Service.SERVICE);
		
		CollectionUtil.doIterator(directoryInfoList, _info -> {
			try
			{
				ServiceProvider.register(_info);
				
				if(_info.getServiceVariableSize() > 0)
				{
					this.logger.warn("Unused service variable list\n{}", _info.toString());
				}
			}
			catch(Exception e)
			{
				this.logger.error("Can not register the service. Class : {}", _info.getClassName());
			}
		});
		
		CollectionUtil.doIterator(this.serviceDirectory.values(), _serviceDir -> {
			try
			{
				_serviceDir.startup();
			}
			catch(Exception e)
			{
				this.logger.error("Can not start the service. Service class : {}", _serviceDir.getClass().toString());
			}
		});
	}

	@Override
	public void stop() throws Exception
	{
		CollectionUtil.doIterator(this.serviceDirectory.values(), _serviceDir -> {
			try
			{
				_serviceDir.shutdown();
			}
			catch(Exception e)
			{
				this.logger.error("Can not stop the service. Service class : {}", _serviceDir.getClass().toString());
			}
		});
	}

	@Override
	public void update(Observable o, Object arg)
	{
		List<ServiceDirectoryInfo> directoryInfoList = 
				SystemSetting.getInstance().getListAndRemove(EnvKey.PreFix.SERVICE, EnvKey.Service.SERVICE);
		
		if(! this.isAutoReload) return;
		
		CollectionUtil.doIterator(directoryInfoList, _info -> {
			try
			{
				Class<?> clazz = Class.forName(_info.getClassName());
				Method[] methods = clazz.getMethods();
				
				for(Method m : methods)
				{
					team.balam.exof.service.annotation.Service serviceAnn = 
							m.getAnnotation(team.balam.exof.service.annotation.Service.class);
					
					if(serviceAnn != null)
					{
						String serviceName = serviceAnn.name();
						if(serviceName.length() == 0) serviceName = m.getName();
						
						Map<String, String> serviceVariable = _info.getVariable(serviceName);
						
						ServiceDirectory serviceDir = this.serviceDirectory.get(_info.getPath());
						if(serviceDir != null)
						{
							serviceDir.reloadVariable(serviceName, serviceVariable);
							
							this.logger.warn("Complete reloading ServiceVariable. [{}]", _info.getPath() + "/" + serviceName);
						}
						
						this.logger.warn("Complete reloading service. [{}]", _info.getPath() + "/" + serviceName);
					}
				}
			}
			catch(Exception e)
			{
				this.logger.error("Can not reload the ServiceVariable. Class : {}", _info.getClassName());
			}
		});
	}
}
