package team.balam.exof.module.listener.handler.transform;

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import team.balam.exof.module.service.ServiceObject;

public class JsonTransform implements ServiceObjectTransform<String>
{
	private ObjectMapper jsonMapper = new ObjectMapper();
	private TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>(){};
	
	@Override
	public ServiceObject transform(String _msg) throws Exception 
	{
		HashMap<String, Object> requestMap = this.jsonMapper.readValue(_msg, this.mapType);
		
		//타입키 관련 클래스추가
		ServiceObject serviceObject = new ServiceObject((String)requestMap.remove(""));
		serviceObject.setRequest(requestMap);
		
		return serviceObject;
	}
	
//	public static void main(String[] _a) throws Exception
//	{
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put(TagKey.SERVICE_PATH, "/test/test");
//		map.put("name", "권성민");
//		map.put("test", 123);
//		
//		ObjectMapper mapper = new ObjectMapper();
//		String value = mapper.writeValueAsString(map);
//		System.out.println(value);
//		
//		TypeReference<HashMap<String, Object>> type = new TypeReference<HashMap<String, Object>>(){};
//		HashMap<String, Object> result = mapper.readValue(value, type);
//		System.out.println(result);
//	}
}
