package team.balam.exof.module.service;

import java.util.*;

/**
 * Created by kwonsm on 2017. 6. 10..<br>
 * Service.xml에서 serviceVariable의 variable을 리스트 형태로 저장한다
 */
public class ServiceVariable {
    private Map<String, List<String>> variable;

    public ServiceVariable() {
        this.variable = new LinkedHashMap<>();
    }

    Set<String> getKeys() {
        return this.variable.keySet();
    }

    Set<Object> getValues() {
        Set<Object> values = new LinkedHashSet<>();
        for (String key : this.variable.keySet()) {
            values.add(this.get(key));
        }

        return values;
    }

    public int size() {
        return this.variable.size();
    }

    public void put(String _key, String _value) {
        List<String> valueList = this.variable.computeIfAbsent(_key, _variableKey -> new LinkedList<>());
        valueList.add(_value);
    }

    /**
     * serviceVariable의 값을 가져온다
     * @param _key serviceVariable의 name
     * @return name이 하나일 경우 String, 여러 개일 경우 List로 반환
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
     * serviceVariable의 값을 가져온다
     * @param _key serviceVariable의 name
     * @return name에 개수에 상관없이 첫 번째 값을 반환
     */
    String getString(String _key) {
        List<String> valueList = this.variable.get(_key);
        if (valueList == null || valueList.isEmpty()) {
            return "";
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
