package team.balam.exof.environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * framework.yaml 의 설정을 저정한다.
 * @author kwonsm
 *
 */
public class SystemSetting {
	private static Map<String, Object> framework = new HashMap<>();
	private static Object external;

	private SystemSetting() {

	}

	public static void setFramework(String _key, Object _value) {
		framework.put(_key, _value);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFramework(String _key) {
		return (T) framework.get(_key);
	}

	public static void setExternal(Object _value) {
		external = _value;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getExternal() {
		return (T) external;
	}
}
