package balam.exof.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.Module;
import balam.exof.environment.EnvKey;
import balam.exof.environment.SystemSetting;
import balam.exof.util.CollectionUtil;

public class ServiceProvider implements Module
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String, ServiceDirectory> serviceDirectory = new HashMap<>();
	
	private static ServiceProvider self = new ServiceProvider();
	
	private ServiceProvider() {}
	
	public static ServiceProvider getInstance()
	{
		return self;
	}
	
	synchronized public static void register(ServiceDirectoryInfo _info) throws Exception
	{
		Class<?> clazz = Class.forName(_info.getClassName());
		balam.exof.service.annotation.ServiceDirectory serviceDirAnn = 
				clazz.getAnnotation(balam.exof.service.annotation.ServiceDirectory.class);
		
		if(serviceDirAnn != null)
		{
			Object host = clazz.newInstance();
			
			ServiceDirectory serdir = self.serviceDirectory.get(_info.getPath());
			if(serdir == null)
			{
				serdir = new ServiceDirectory();
				self.serviceDirectory.put(_info.getPath(), serdir);
			}
			
			Method[] method = clazz.getMethods();
			for(Method m : method)
			{
				balam.exof.service.annotation.Service serviceAnn = 
						m.getAnnotation(balam.exof.service.annotation.Service.class);
				
				if(serviceAnn != null)
				{
					String serviceName = serviceAnn.name();
					if(serviceName.length() == 0) serviceName = m.getName();
					
					serdir.register(serviceName, host, m, _info.getVariable(serviceName));
				}
			}
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
	}

	@Override
	public void stop() throws Exception
	{
		
	}
}
