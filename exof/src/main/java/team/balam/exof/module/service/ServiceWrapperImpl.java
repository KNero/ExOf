package team.balam.exof.module.service;

import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.Outbound;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ServiceWrapperImpl implements ServiceWrapper {
	private Method method;
	private Object host;
	private int methodParamCount;
	
	private List<Inbound> inbound = new ArrayList<>(5);
	private List<Outbound<?, ?>> outbound = new ArrayList<>(5);

	@Override
	public String getMethodName() {
		return this.method.getName();
	}

	void setMethod(Method method) {
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

	void addInbound(Inbound _in) {
		this.inbound.add(_in);
	}

	void addOutbound(Outbound<?, ?> _out) {
		this.outbound.add(_out);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void call(ServiceObject _so) throws Exception {
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
