package balam.exof.service;

import java.lang.reflect.Method;
import java.util.Map;

public class ServiceImpl implements Service
{
	private Method method;
	private Object host;
	private Map<String, String> variable;
	
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

	@Override
	public void call(ServiceObject _so) throws Exception
	{
		
	}
}
