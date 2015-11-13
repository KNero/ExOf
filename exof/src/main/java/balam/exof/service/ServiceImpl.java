package balam.exof.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.service.component.Inbound;
import balam.exof.service.component.Outbound;
import balam.exof.util.CollectionUtil;

public class ServiceImpl implements Service
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
			
	private Method method;
	private Object host;
	private Map<String, String> variable;
	
	private List<Inbound> inbound = new ArrayList<Inbound>(5);
	private List<Outbound> outbound = new ArrayList<Outbound>(5);
	
	public Method getMethod()
	{
		return method;
	}

	public void setMethod(Method method)
	{
		this.method = method;
	}

	public Object getHost()
	{
		return host;
	}

	public void setHost(Object host)
	{
		this.host = host;
	}

	public Map<String, String> getVariable()
	{
		return variable;
	}

	public void setVariable(Map<String, String> variable)
	{
		this.variable = variable;
	}
	
	public void addInbound(Inbound _in)
	{
		this.inbound.add(_in);
	}
	
	public void addOutbound(Outbound _out)
	{
		this.outbound.add(_out);
	}

	@Override
	public void call(ServiceObject _so) throws Exception
	{
		_so.setVariables(this.variable);
		
		CollectionUtil.doIterator(this.inbound, _in -> {
			try
			{
				_in.execute(_so);
			}
			catch(Exception e)
			{
				this.logger.error("Inbound error.", e);
			}
		});
		
		Object result = this.method.invoke(this.host, _so.getServiceParameter());
		
		if(result != null)
		{
			CollectionUtil.doIterator(this.outbound, _out -> {
				try
				{
					_out.execute(result);
				}
				catch(Exception e)
				{
					this.logger.error("Outbound error.", e);
				}
			});
		}
	}
}
