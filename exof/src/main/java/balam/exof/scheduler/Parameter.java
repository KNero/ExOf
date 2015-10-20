package balam.exof.scheduler;

import java.util.HashMap;
import java.util.Map;

public class Parameter 
{
	private Map<String, Object> param;
	
	public Parameter()
	{
		 this.param = new HashMap<>();
	}
	
	public Parameter(Map<String, Object> _param)
	{
		this.param = _param;
	}
	
	public void set(String _key, String _value)
	{
		this.param.put(_key, _value);
	}
	
	public Object get(String _key)
	{
		return this.param.get(_key);
	}
	
	public String getString(String _key)
	{
		return this.param.toString();
	}
	
	public Integer getInteger(String _key)
	{
		return (Integer)this.param.get(_key);
	}
	
	public Boolean getBoolean(String _key)
	{
		return (Boolean)this.param.get(_key);
	}
}
