package team.balam.exof.environment.vo;

import team.balam.exof.Constant;

import java.util.*;

public class ServiceVariableInfo {
	private boolean isNull;
	private Map<String, List<String>> variables = new HashMap<>();

	public static final ServiceVariableInfo NULL_OBJECT = new ServiceVariableInfo();

	private ServiceVariableInfo() {
		this.isNull = true;
	}

	public ServiceVariableInfo(List<Map<String, Object>> dbList) {
		for (Map<String, Object> variable : dbList) {
			String key = (String) variable.get("key");
			String value = (String) variable.get("value");

			List<String> variableList = this.variables.computeIfAbsent(key, _key -> new ArrayList<>());
			variableList.add(value);
		}
	}

	public String getStringValue(String _key) {
		List<String> result = this.variables.get(_key);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		} else {
			return Constant.EMPTY_STRING;
		}
	}

	public List<String> getListValue(String _key) {
		List<String> result = this.variables.get(_key);
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return Collections.emptyList();
		}
	}
}
