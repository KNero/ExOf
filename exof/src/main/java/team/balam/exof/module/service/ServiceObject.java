package team.balam.exof.module.service;

import team.balam.exof.environment.vo.ServiceVariable;

public final class ServiceObject
{
	protected Object request;
	private String servicePath;
	protected ServiceVariable serviceVariables;
	protected Object[] serviceParameter;
	
	private boolean isAutoCloseSession;
	private boolean isCloseSessionByError;
	
	public ServiceObject(String _path)
	{
		this.servicePath = _path;
	}
	
	final public String getServicePath()
	{
		return this.servicePath;
	}
	
	public void setServicePath(String servicePath)
	{
		this.servicePath = servicePath;
	}
	
	public void setServiceVariables(ServiceVariable _variable)
	{
		this.serviceVariables = _variable;
	}
	
	public ServiceVariable getServiceVariables()
	{
		return this.serviceVariables;
	}
	
	public void setServiceParameter(Object[] _parameter)
	{
		this.serviceParameter = _parameter;
	}
	
	public void setRequest(Object _request) 
	{
		this.request = _request;
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
	 * @param isAutoCloseSession
	 */
	public void setAutoCloseSession(boolean isAutoCloseSession)
	{
		this.isAutoCloseSession = isAutoCloseSession;
	}

	public boolean isCloseSessionByError()
	{
		return isCloseSessionByError;
	}

	/**
	 * 에러가 발생했을 경우 클라이언트 세션을 닫을지 여부
	 * @param isCloseSessionByError
	 */
	public void setCloseSessionByError(boolean isCloseSessionByError)
	{
		this.isCloseSessionByError = isCloseSessionByError;
	}

	/**
	 * 이 메소드를 통해서 호출한 서비스 메소드의 파라미터를 세팅한다.<br/><br/>
	 * 서비스 메소드를 호출 할 때 아래와 같은 규칙으로 파라미터가 세팅된다.<br/>
	 * 1. 파라미터가 있다면 : 파라미터 (setServiceParameter를 사용하여 세팅)<br/>
	 * 2. 수신 받은 메시지와 서비스 변수 값이 있다면 : 메시지, 변수1, 변수2 ...<br/>
	 * 3. 수신받은 메시지만 있을 경우 : 메시지<br/>
	 * 4. 서비스 변수만 있을 경우 : 변수1, 변수2 ...<br/>
	 * @return 메소드를 호출할 때 사용될 파라미터
	 */
	final Object[] getServiceParameter()
	{
		if(this.serviceParameter != null) return this.serviceParameter;
		
		if(this.request != null && this.serviceVariables != null)
		{
			Object[] param = new Object[this.serviceVariables.size() + 1];
			param[0] = this.request;
			
			int i = 1;
			for(Object value : this.serviceVariables.getValues())
			{
				param[i++] = value;
			}
			
			return param;
		}
		else if(this.request != null)
		{
			return new Object[]{this.request};
		}
		else if(this.serviceVariables != null)
		{
			Object[] param = new Object[this.serviceVariables.size()];
			
			int i = 0;
			for(Object value : this.serviceVariables.getValues())
			{
				param[i++] = value;
			}
			
			return param;
		}
		
		return null;
	}
}
