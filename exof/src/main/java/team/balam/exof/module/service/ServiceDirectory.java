package team.balam.exof.module.service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDirectory
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String dirPath;
	private Map<String, Service> serviceMap = new ConcurrentHashMap<>();
	
	public ServiceDirectory(String _dirPath)
	{
		this.dirPath = _dirPath;
	}
	
	public void loadStartupAndShutdown(Map<String, Method> _startup, Map<String, Method> _shutdown)
	{
		this.serviceMap.forEach((_serviceName, _service) -> {
			Method startup = _startup.get(_serviceName);
			Method shutdown = _shutdown.get(_serviceName);
			
			ServiceImpl serviceImpl = (ServiceImpl)_service;
			serviceImpl.setStartupAndShutdown(startup, shutdown);
		});
	}
	
	public void startupAllServices()
	{
		this.serviceMap.forEach((_serviceName, _service) -> {
			try
			{
				_service.startup();
			}
			catch(Exception e)
			{
				this.logger.error("Can not start the service. Service name : {}", _serviceName, e);
			}
		});
	}
	
	public void shutdownAllServices()
	{
		this.serviceMap.forEach((_serviceName, _service) -> {
			try
			{
				_service.shutdown();
			}
			catch(Exception e)
			{
				this.logger.error("Can not stop the service. Service name : {}", _serviceName, e);
			}
		});
	}
	
	public ServiceImpl register(String _serviceName, Object _host, Method _method, Map<String, String> _variable)
		throws ServiceAlreadyExistsException
	{
		if(this.serviceMap.containsKey(_serviceName))
		{
			throw new ServiceAlreadyExistsException(this.dirPath + "/" + _serviceName);
		}
		
		ServiceImpl service = new ServiceImpl();
		service.setHost(_host);
		service.setMethod(_method);
		service.setVariable(_variable);
		
		this.serviceMap.put(_serviceName, service);
		
		return service;
	}
	
	public Service getService(String _serviceName)
	{
		return this.serviceMap.get(_serviceName);
	}
	
	public void reloadVariable(String _serviceName, Map<String, String> _variable)
	{
		ServiceImpl service = (ServiceImpl)this.serviceMap.get(_serviceName);
		if(service != null)
		{
			service.setVariable(_variable);
		}
	}
}
