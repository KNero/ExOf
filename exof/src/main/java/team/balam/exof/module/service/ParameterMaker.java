package team.balam.exof.module.service;

import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.annotation.Variable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ParameterMaker {
	private int parameterCount;
	private Map<String, Integer> variableIndex = new HashMap<>();
	private Map<Class<?>, Integer> typeIndex = new HashMap<>();
	private int serviceObjectIndex = -1;

	ParameterMaker(Method method) {
		Parameter[] parameters = method.getParameters();
		parameterCount = parameters.length;

		for (int p = 0; p < parameters.length; ++p) {
			Parameter param = parameters[p];
			typeIndex.put(param.getType(), p);

			if (param.getType() == ServiceObject.class) {
				serviceObjectIndex = p;
				continue;
			}

			Variable variable = param.getAnnotation(Variable.class);
			if (variable != null) {
				if (variable.value().isEmpty()) {
					LoggerFactory.getLogger(ServiceWrapperImpl.class).error("===> Parameter Variable annotation must have value.");
				}

				variableIndex.put(variable.value(), p);
			}
		}
	}

	Object[] getParameter(ServiceObject so) {
		Object[] serviceParameter = so.getServiceParameter();
		if (serviceParameter != null) {
			return serviceParameter;
		}

		Object[] methodParameter = new Object[parameterCount];
		if (serviceObjectIndex > -1) {
			methodParameter[serviceObjectIndex] = so;
		}

		List<Object> parameterValues = so.getParameterValues();
		parameterValues.add(so.getRequest());
		for (Object value : parameterValues) {
			for (Map.Entry<Class<?>, Integer> typeInfo : typeIndex.entrySet()) {
				if (typeInfo.getKey().isInstance(value)) {
					methodParameter[typeInfo.getValue()] = value;
				}
			}
		}

		for (Map.Entry<String, Integer> entry : variableIndex.entrySet()) {
			Object value = findValue(entry.getKey(), so, parameterValues);
			if (value != null && entry.getValue() < parameterCount) {
				methodParameter[entry.getValue()] = value;

			}
		}

		return methodParameter;
	}

	private static Object findValue(String key, ServiceObject serviceObject, List<Object> parameterValues) {
		String value = serviceObject.getPathVariable(key);
		if (value != null) {
			return value;
		}

		for (Object paramValue : parameterValues) {
			if (paramValue instanceof Map) {
				Map m = (Map) paramValue;
				Object v = m.get(key);
				if (v != null) {
					return v;
				}
			}
		}

		return null;
	}
}
