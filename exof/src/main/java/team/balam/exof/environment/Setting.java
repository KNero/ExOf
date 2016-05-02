package team.balam.exof.environment;

import java.util.List;
import java.util.Map;

public interface Setting 
{
	void set(String _prefix, String _key, Object _value);
	Object get(String _prefix, String _key);
	Object getAndRemove(String _prefix, String _key);
	
	String getString(String _prefix, String _key);
	String getStringAndRemove(String _prefix, String _key);
	
	<T> List<T> getList(String _prefix, String _key);
	<T> List<T> getListAndRemove(String _prefix, String _key);
	
	<K, V> Map<K, V> getMap(String _prefix, String _key);
	<K, V> Map<K, V> getMapAndRemove(String _prefix, String _key);
}
