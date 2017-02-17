package team.balam.exof.environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 시스템의 모든 설정을 저장
 * @author kwonsm
 *
 */
public class SystemSetting
{
	private Map<String, Object> setting = new HashMap<String, Object>();
	
	private static SystemSetting self = new SystemSetting();
	
	private SystemSetting() {}
	
	public static SystemSetting getInstance()
	{
		return self;
	}
	
	public void set(String _prefix, String _key, Object _value) 
	{
		this.setting.put(_prefix + _key, _value);
	}
	
	public Object get(String _prefix, String _key)
	{
		return this.setting.get(_prefix + _key);
	}

	public String getString(String _prefix, String _key) 
	{
		return this.setting.get(_prefix + _key).toString();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String _prefix, String _key) 
	{
		return (List<T>)this.setting.get(_prefix + _key);
	}

	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getMap(String _prefix, String _key) 
	{
		return (Map<K, V>)this.setting.get(_prefix + _key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getFramework(String _key)
	{
		return (T)this.setting.get(EnvKey.PreFix.FRAMEWORK + _key);
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
