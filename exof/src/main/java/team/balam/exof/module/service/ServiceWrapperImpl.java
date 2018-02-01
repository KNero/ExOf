package team.balam.exof.module.service;

import team.balam.exof.environment.vo.ServiceVariable;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.Outbound;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ServiceWrapperImpl implements ServiceWrapper {
	private Method method;
	private Object host;
	private int methodParamCount;
	private volatile ServiceVariable variable;
	
	private List<Inbound> inbound = new ArrayList<>(5);
	private List<Outbound<?, ?>> outbound = new ArrayList<>(5);
	
	@Override
	public Object getServiceVariable(String _name) {
		return this.variable.get(_name);
	}
	
	@Override
	public Set<String> getServiceVariableKeys() {
		if (this.variable != null) {
			return this.variable.getKeys();
		} else {
			return Collections.emptySet();
		}
	}
	
	public Method getMethod()
	{
		return method;
	}

	public void setMethod(Method method)
	{
		this.method = method;
		this.methodParamCount = this.method.getParameterCount();
	}

	@SuppressWarnings("unchecked")
	public <T> T getHost()
	{
		return (T) host;
	}

	void setHost(Object host)
	{
		this.host = host;
	}

	public void setVariable(ServiceVariable variable) {
		this.variable = variable;
	}

	void addInbound(Inbound _in) {
		this.inbound.add(_in);
	}

	void addOutbound(Outbound<?, ?> _out) {
		this.outbound.add(_out);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void call(ServiceObject _so) throws Exception {
		if (this.variable != null) {
			_so.setServiceVariables(this.variable.clone());
		}

		for (Inbound in : this.inbound) {
			in.execute(_so);
		}

		Object[] methodParameter = null;
		if (this.methodParamCount > 0) {
			methodParameter = _so.getServiceParameter();
		}

		Object result = this.method.invoke(this.host, methodParameter);
		if (result != null) {
			for (Outbound outbound : this.outbound) {
				result = outbound.execute(result);
			}

			if (result != null) {
				RequestContext.writeAndFlushResponse(result);
			}
		}
	}
}
