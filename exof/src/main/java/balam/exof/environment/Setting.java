package balam.exof.environment;

import java.util.List;
import java.util.Map;

public interface Setting 
{
	interface PreFix
	{
		String FRAMEWORK = "framework.";
		String SERVICE = "service.";
	}
	
	void set(String _prefix, String _key, Object _value);
	Object get(String _prefix, String _key);
	Object getAndRemove(String _prefix, String _key);
	
	String getString(String _prefix, String _key);
	String getStringAndRemove(String _prefix, String _key);
	
	List<?> getList(String _prefix, String _key);
	List<?> getListAndRemove(String _prefix, String _key);
	
	Map<?, ?> getMap(String _prefix, String _key);
	Map<?, ?> getMapAndRemove(String _prefix, String _key);
}
