package team.balam.exof.environment.vo;

import team.balam.exof.Constant;

import java.util.*;

/**
 * Created by kwonsm on 2017. 6. 10..<br>
 * Service.xml 에서 serviceVariable 의 variable 을 리스트 형태로 저장한다
 */
public class ServiceVariable {
    private Map<String, List<String>> variable = new LinkedHashMap<>();
	private boolean isNull;

	public static final ServiceVariable NULL_OBJECT = new ServiceVariable();

	private ServiceVariable() {
		this.isNull = true;
	}

	public ServiceVariable(List<Map<String, Object>> _dbList) {
		for (Map<String, Object> variable : _dbList) {
			String key = (String) variable.get("key");
			String value = (String) variable.get("value");

			List<String> variableList = this.variable.computeIfAbsent(key, _key -> new ArrayList<>());
			variableList.add(value);
		}
	}

	public Set<String> getKeys() {
        return this.variable.keySet();
    }

	public boolean isNull() {
		return isNull;
	}

	public int size() {
        return this.variable.size();
    }

    /**
     * serviceVariable 의 값을 가져온다
     * @param _key serviceVariable 의 name
     * @return name 이 하나일 경우 String, 여러 개일 경우 List 로 반환
     */
    public Object get(String _key) {
        List<String> valueList = this.variable.get(_key);
        if (valueList != null && !valueList.isEmpty()) {
            if (valueList.size() == 1) {
                return valueList.get(0);
            } else {
                return valueList;
            }
        } else {
            return null;
        }
    }

    /**
     * serviceVariable 의 값을 가져온다. ServiceProvider 에서 serviceVariable 의 모든 값을 String 으로 만들 때 쓰인다
     * console 및 webConsole monitoring 에서 사용된다
     * @param _key serviceVariable 의 name
     * @return name 개수에 상관없이 첫 번째 값을 반환
     */
    public String getString(String _key) {
        List<String> valueList = this.variable.get(_key);
        if (valueList == null || valueList.isEmpty()) {
            return Constant.EMPTY_STRING;
        } else {
            return valueList.get(0);
        }
    }

    @Override
    public String toString() {
        StringBuilder log = new StringBuilder();
        for (Map.Entry<String, List<String>> info : this.variable.entrySet()) {
            log.append(info.getKey()).append("=").append(info.getValue()).append("\n");
        }

        return log.toString();
    }
}
