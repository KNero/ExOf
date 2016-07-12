package team.balam.exof.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceDirectory
{
	private String dirPath;
	private Map<String, Service> serviceMap = new ConcurrentHashMap<>();
	
	private Object host;
	private Method startup;
	private Method shutdown;
	
	public void startup() throws Exception
	{
		if(this.startup != null)
		{
			this.startup.invoke(this.host);
		}
	}
	
	public void shutdown() throws Exception
	{
		if(this.shutdown != null)
		{
			this.shutdown.invoke(this.host);
		}
	}
	
	public void setHost(Object host) 
	{
		this.host = host;
	}

	public void setStartup(Method startup) 
	{
		this.startup = startup;
	}

	public void setShutdown(Method shutdown) 
	{
		this.shutdown = shutdown;
	}

	public ServiceDirectory(String _dirPath)
	{
		this.dirPath = _dirPath;
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
	
	public Collection<Service> getServices()
	{
		return this.serviceMap.values();
	}
}
