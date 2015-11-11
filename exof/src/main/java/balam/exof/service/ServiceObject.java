package balam.exof.service;

import java.util.Map;

public class ServiceObject
{
	private Object request;
	protected String servicePath;
	protected Map<String, String> variables;
	protected Object[] parameter;
	
	public ServiceObject(String _path)
	{
		this.servicePath = _path;
	}
	
	public String getServicePath()
	{
		return this.servicePath;
	}
	
	public void setVariables(Map<String, String> _variable)
	{
		this.variables = _variable;
	}
	
	public Map<String, String> getVariables()
	{
		return this.variables;
	}
	
	public void setServiceParameter(Object[] _parameter)
	{
		this.parameter = _parameter;
	}
	
	public void setRequest(Object _request) 
	{
		this.request = _request;
	}
	
	public Object getRequest()
	{
		return this.request;
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
	public Object[] getServiceParameter()
	{
		if(this.parameter != null) return this.parameter;
		
		if(this.request != null && this.variables != null)
		{
			Object[] param = new Object[this.variables.size() + 1];
			param[0] = this.request;
			
			int i = 1;
			for(String value : this.variables.values())
			{
				param[i++] = value;
			}
			
			return param;
		}
		else if(this.request != null && this.variables == null)
		{
			return new Object[]{this.request};
		}
		else if(this.request == null && this.variables != null)
		{
			Object[] param = new Object[this.variables.size()];
			
			int i = 0;
			for(String value : this.variables.values())
			{
				param[i++] = value;
			}
			
			return param;
		}
		
		return null;
	}
}
