package team.balam.exof.module.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kwonsm on 2017. 6. 10..
 */
public class ServiceVariable {
    private Map<String, List<String>> variable;

    public ServiceVariable() {
        this.variable = new LinkedHashMap<>();
    }

    public Set<String> getKeys() {
        return this.variable.keySet();
    }

    public Set<Object> getValues() {
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
        List<String> valueList = this.variable.get(_key);
        if (valueList == null) {
            valueList = new LinkedList<>();
            this.variable.put(_key, valueList);
        }

        valueList.add(_value);
    }

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

    public String getString(String _key) {
        List<String> valueList = this.variable.get(_key);
        if (valueList == null || valueList.isEmpty()) {
            return "";
        } else {
            return valueList.get(0);
        }
    }

    public List<String> getList(String _key) {
        List<String> valueList = this.variable.get(_key);
        if (valueList == null) {
            return Collections.emptyList();
        } else {
            return valueList;
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
