package balam.exof.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ServiceDirectory
{
	private Map<String, Service> serviceMap = new HashMap<>();
	
	public void register(String _serviceName, Object _host, Method _method, Map<String, String> _variable)
	{
		ServiceImpl service = new ServiceImpl();
		service.setHost(_host);
		service.setMethod(_method);
		service.setVariable(_variable);
		
		this.serviceMap.put(_serviceName, service);
	}
}
