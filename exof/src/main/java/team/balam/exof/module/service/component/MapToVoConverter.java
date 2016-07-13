package team.balam.exof.module.service.component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MapToVoConverter
{
	private Class<?> voClass;
	private HashMap<String, Method> methodMap = new HashMap<>();
	
	public void init(String _class) throws Exception
	{
		this.voClass = Class.forName(_class);
		Method[] methods = this.voClass.getMethods();
		
		for(Method m : methods)
		{
			String methodName = m.getName();
			
			if(methodName.startsWith("set"))
			{
				this.methodMap.put(methodName.substring(3), m);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object convert(Object _req) throws Exception
	{
		if(! (_req instanceof Map<?, ?>))
		{
			throw new Exception("Service Rquest must is Map for use MapToVoConverter.");
		}
		
		Map<String, Object> mapReq = (Map<String, Object>)_req;
		
		Object vo = this.voClass.newInstance();
		
		Set<String> keySet = mapReq.keySet();
		for(String key : keySet)
		{
			Object value = mapReq.get(key);
			Method method = this.methodMap.get(key);
			
			method.invoke(vo, value);
		}
		
		return vo;
	}
}
