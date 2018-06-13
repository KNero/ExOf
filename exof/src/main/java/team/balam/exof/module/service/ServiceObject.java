package team.balam.exof.module.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ServiceObject
{
	private Object request;
	private String servicePath;
	private String serviceGroupId;

	private Object[] serviceParameter;
	private Map<String, String> pathVariable = new HashMap<>();
	private List<Object> parameterValues = new ArrayList<>(5);
	
	private boolean isAutoCloseSession;
	private boolean isCloseSessionByError;

	public ServiceObject() {

	}

	public ServiceObject(String path) {
		this.servicePath = path;
	}

	final void setPathVariable(Map<String, String> pathVariable) {
		this.pathVariable = pathVariable;
	}

	public final String getPathVariable(String key) {
		return pathVariable.get(key);
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

	public void addParameterValue(Object value) {
		parameterValues.add(value);
	}

	public List<Object> getParameterValues() {
		return parameterValues;
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

	public String getServiceGroupId() {
		return serviceGroupId;
	}

	public void setServiceGroupId(String serviceGroupId) {
		this.serviceGroupId = serviceGroupId;
	}

	/**
	 * 에러가 발생했을 경우 클라이언트 세션을 닫을지 여부
	 */
	public void setCloseSessionByError(boolean isCloseSessionByError) {
		this.isCloseSessionByError = isCloseSessionByError;
	}

	public Object[] getServiceParameter() {
		return serviceParameter;
	}
}
