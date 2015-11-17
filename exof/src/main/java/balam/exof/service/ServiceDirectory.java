package balam.exof.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ServiceDirectory
{
	private String dirPath;
	private Map<String, Service> serviceMap = new HashMap<>();
	
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
}
