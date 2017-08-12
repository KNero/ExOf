package team.balam.exof.module.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.environment.vo.ServiceVariable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceDirectory
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Object host;
	private String dirPath;
	
	private Method startup;
	private Method shutdown;
	
	private Map<String, Service> serviceMap = new ConcurrentHashMap<>();
	
	ServiceDirectory(Object _host, String _dirPath)
	{
		this.host = _host;
		this.dirPath = _dirPath;
	}
	
	void startup()
	{
		if(this.startup != null)
		{
			try
			{
				Class<?>[] param = this.startup.getParameterTypes();
				if(param.length ==1 && param[0].equals(Map.class))
				{
					this.startup.invoke(this.host, this.serviceMap);
				}
				else
				{
					this.startup.invoke(this.host);
				}
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
	
	void setStartup(Method startup)
	{
		this.startup = startup;
	}
	
	void setShutdown(Method shutdown)
	{
		this.shutdown = shutdown;
	}

	ServiceImpl register(String _serviceName, Object _host, Method _method, ServiceVariable _variable)
			throws ServiceAlreadyExistsException {

		if (this.serviceMap.containsKey(_serviceName)) {
			throw new ServiceAlreadyExistsException(this.dirPath + "/" + _serviceName);
		}

		ServiceImpl service = new ServiceImpl();
		service.setHost(_host);
		service.setMethod(_method);
		service.setVariable(_variable);

		this.serviceMap.put(_serviceName, service);

		return service;
	}
	
	Service getService(String _serviceName)
	{
		return this.serviceMap.get(_serviceName);
	}
	
	Set<String> getServiceNameList()
	{
		return this.serviceMap.keySet();
	}

	void reloadVariable(String _serviceName, ServiceVariable _variable) {
		ServiceImpl service = (ServiceImpl) this.serviceMap.get(_serviceName);
		if (service != null) {
			service.setVariable(_variable);
		}
	}
}
