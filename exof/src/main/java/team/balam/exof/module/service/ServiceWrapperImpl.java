package team.balam.exof.module.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.Outbound;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ServiceWrapperImpl implements ServiceWrapper {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceWrapperImpl.class);

	private Method method;
	private Object host;
	private int methodParamCount;
	private boolean isInternal;
	
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

	void setInternal(boolean internal) {
		isInternal = internal;
	}

	@Override
	public boolean isInternal() {
		return this.isInternal;
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

	void addInbound(Inbound in) {
		this.inbound.add(in);
	}

	void addOutbound(Outbound<?, ?> out) {
		this.outbound.add(out);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public <T> T call(ServiceObject so) {
		try {
			for (Inbound in : this.inbound) {
				in.execute(so);
			}

			Object[] methodParameter = null;
			if (this.methodParamCount > 0) {
				methodParameter = so.getServiceParameter();
			}

			Object result = this.method.invoke(this.host, methodParameter);
			if (result != null) {
				for (Outbound out : this.outbound) {
					result = out.execute(result);
				}

				return (T) result;
			}
		} catch (Exception e) {
			LOG.error("Fail to call service.", e);
		}

		return null;
	}
}
