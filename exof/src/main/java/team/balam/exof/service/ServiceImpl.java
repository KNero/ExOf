package team.balam.exof.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.service.component.Inbound;
import team.balam.exof.service.component.MapToVoConverter;
import team.balam.exof.service.component.Outbound;
import team.balam.exof.util.CollectionUtil;

public class ServiceImpl implements Service
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
			
	private Method method;
	private Object host;
	private int methodParamCount;
	private Map<String, String> variable;
	
	private List<Inbound> inbound = new ArrayList<>(5);
	private List<Outbound<?, ?>> outbound = new ArrayList<>(5);
	private MapToVoConverter mapToVoConverter;
	
	public Method getMethod()
	{
		return method;
	}

	public void setMethod(Method method)
	{
		this.method = method;
		this.methodParamCount = this.method.getParameterTypes().length;
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
	
	public void addOutbound(Outbound<?, ?> _out)
	{
		this.outbound.add(_out);
	}
	
	public void setMapToVoConverter(String _class) throws Exception
	{
		this.mapToVoConverter = new MapToVoConverter();
		this.mapToVoConverter.init(_class);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void call(ServiceObject _so) throws Exception
	{
		_so.setServiceVariables(this.variable);
		
		if(this.mapToVoConverter != null)
		{
			Object vo = this.mapToVoConverter.convert(_so.getRequest());
			_so.setRequest(vo);
		}
		
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
		
		Object[] methodParameter = null;
		if(this.methodParamCount > 0) methodParameter = _so.getServiceParameter();
		
		Object result = this.method.invoke(this.host, methodParameter);
		
		if(result != null)
		{
			try
			{
				for(Outbound outbound : this.outbound)
				{
					result = outbound.execute(result);
				}
			}
			catch(Exception e)
			{
				this.logger.error("Outbound error.", e);
			}
		}
	}
}
