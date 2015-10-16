package balam.exof.environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 시스템의 모든 설정을 저장
 * @author kwonsm
 *
 */
public class SystemSetting implements Setting
{
	private Map<String, Object> setting = new HashMap<String, Object>();
	
	private static SystemSetting self = new SystemSetting();
	
	private SystemSetting() {}
	
	public static Setting getInstance()
	{
		return self;
	}
	
	@Override
	public void set(String _prefix, String _key, Object _value) 
	{
		this.setting.put(_prefix + _key, _value);
	}
	
	@Override
	public Object get(String _prefix, String _key)
	{
		return this.setting.get(_prefix + _key);
	}

	@Override
	public String getString(String _prefix, String _key) 
	{
		return (String)this.setting.get(_prefix + _key);
	}

	@Override
	public String getStringAndRemove(String _prefix, String _key) 
	{
		return (String)this.setting.remove(_prefix + _key);
	}

	@Override
	public List<?> getList(String _prefix, String _key) 
	{
		return (List<?>)this.setting.get(_prefix + _key);
	}

	@Override
	public List<?> getListAndRemove(String _prefix, String _key) 
	{
		return (List<?>)this.setting.remove(_prefix + _key);
	}

	@Override
	public Map<?, ?> getMap(String _prefix, String _key) 
	{
		return (Map<?, ?>)this.setting.get(_prefix + _key);
	}

	@Override
	public Map<?, ?> getMapAndRemove(String _prefix, String _key) 
	{
		return (Map<?, ?>)this.setting.remove(_prefix + _key);
	}
	
	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();
		for(String key : this.setting.keySet())
		{
			string.append(key).append(" = ").append(this.setting.get(key)).append("\n");
		}
		
		return string.toString();
	}
}
