package balam.exof.scheduler;

import java.util.HashMap;
import java.util.Map;

import balam.exof.util.CollectionUtil;

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
	
	public void set(String _key, Object _value)
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
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		
		CollectionUtil.doIterator(this.param.keySet(), _key -> {
			str.append("[").append(_key).append("]=[").append(this.param.get(_key)).append("]\n");
		});
		
		return str.toString();
	}
}
