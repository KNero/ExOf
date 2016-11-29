package team.balam.exof.module.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDirectory
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Object host;
	private String dirPath;
	
	private Method startup;
	private Method shutdown;
	
	private Map<String, Service> serviceMap = new ConcurrentHashMap<>();
	
	public ServiceDirectory(Object _host, String _dirPath)
	{
		this.host = _host;
		this.dirPath = _dirPath;
	}
	
	public void startup()
	{
		if(this.startup != null)
		{
			try
			{
				this.startup.invoke(this.host);
			}
			catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				this.logger.error("Can not start the service. ServiceDirectory path : {}", this.dirPath, e);
			}
		}
	}
	
	public void shutdown()
	{
		if(this.shutdown != null)
		{
			try
			{
				this.shutdown.invoke(this.host);
			}
			catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				this.logger.error("Can not stop the service. ServiceDirectory path : {}", this.dirPath, e);
			}
		}
	}
	
	public void setStartup(Method startup)
	{
		this.startup = startup;
	}
	
	public void setShutdown(Method shutdown)
	{
		this.shutdown = shutdown;
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
	
	public Set<String> getServiceNameList()
	{
		return this.serviceMap.keySet();
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
