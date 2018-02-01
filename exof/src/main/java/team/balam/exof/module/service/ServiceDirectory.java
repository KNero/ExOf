package team.balam.exof.module.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.environment.vo.ServiceVariable;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ServiceDirectory
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Object host;
	private String dirPath;
	
	private Method startup;
	private Method shutdown;
	
	private Map<String, ServiceWrapper> serviceMap = new ConcurrentHashMap<>();
	
	ServiceDirectory(Object _host, String _dirPath) {
		this.host = _host;
		this.dirPath = _dirPath;
	}
	
	void startup() {
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
	
	void shutdown() {
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

	void register(String _serviceName, Object _host, Method _method, ServiceVariable _variable) throws Exception {
		if (this.serviceMap.containsKey(_serviceName)) {
			throw new ServiceAlreadyExistsException(this.dirPath + "/" + _serviceName);
		}

		ServiceWrapperImpl service = new ServiceWrapperImpl();
		service.setHost(_host);
		service.setMethod(_method);
		service.setVariable(_variable);

		this._checkInboundAnnotation(_method, service);
		this._checkOutboundAnnotation(_method, service);

		this.serviceMap.put(_serviceName, service);
	}

	private void _checkInboundAnnotation(Method _method, ServiceWrapperImpl _service) throws Exception {
		Inbound inboundAnn = _method.getAnnotation(Inbound.class);
		if (inboundAnn != null) {
			for (Class<? extends team.balam.exof.module.service.component.Inbound> clazz : inboundAnn.value()) {
				_service.addInbound(clazz.newInstance());
			}
		}
	}

	private void _checkOutboundAnnotation(Method _method, ServiceWrapperImpl _service) throws Exception {
		Outbound outboundAnn = _method.getAnnotation(Outbound.class);
		if (outboundAnn != null) {
			for (Class<? extends team.balam.exof.module.service.component.Outbound<?, ?>> clazz : outboundAnn.value())
			_service.addOutbound(clazz.newInstance());
		}
	}
	
	ServiceWrapper getService(String _serviceName)
	{
		return this.serviceMap.get(_serviceName);
	}
}
