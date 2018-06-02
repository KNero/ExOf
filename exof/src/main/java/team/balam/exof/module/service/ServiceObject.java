package team.balam.exof.module.service;

import java.util.HashMap;
import java.util.Map;

public final class ServiceObject
{
	private Object request;
	private String servicePath;
	private Object[] serviceParameter;
	private Map<String, String> pathVariable = new HashMap<>();
	
	private boolean isAutoCloseSession;
	private boolean isCloseSessionByError;

	public ServiceObject() {

	}

	final void setPathVariable(Map<String, String> pathVariable) {
		this.pathVariable = pathVariable;
	}

	public final String getPathVariable(String key) {
		return pathVariable.get(key);
	}

	public ServiceObject(String path)
	{
		this.servicePath = path;
	}

	public final String getServicePath()
	{
		return this.servicePath;
	}
	
	public void setServicePath(String servicePath)
	{
		this.servicePath = servicePath;
	}
	
	public void setServiceParameter(Object... parameter)
	{
		this.serviceParameter = parameter;
	}
	
	public void setRequest(Object request)
	{
		this.request = request;
	}
	
	public Object getRequest()
	{
		return this.request;
	}
	
	public boolean isAutoCloseSession()
	{
		return isAutoCloseSession;
	}

	/**
	 * 정상/비정상 종료시 현재 클라이언트 세션을 닫을지 여부
	 */
	public void setAutoCloseSession(boolean isAutoCloseSession)
	{
		this.isAutoCloseSession = isAutoCloseSession;
	}

	public boolean isCloseSessionByError() {
		return isCloseSessionByError;
	}

	/**
	 * 에러가 발생했을 경우 클라이언트 세션을 닫을지 여부
	 */
	public void setCloseSessionByError(boolean isCloseSessionByError) {
		this.isCloseSessionByError = isCloseSessionByError;
	}

	final Object[] getServiceParameter() {
		if(this.serviceParameter != null) {
			return this.serviceParameter;
		} else if (this.request != null) {
			return new Object[]{this.request};
		} else {
			return null;
		}
	}
}
