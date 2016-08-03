package team.balam.exof.module.listener.handler.transform;

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import team.balam.exof.module.service.ServiceObject;

public class JsonTransform implements ServiceObjectTransform<String>
{
	protected ObjectMapper jsonMapper = new ObjectMapper();
	protected TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>(){};
	
	protected String servicePathKey = "servicePath";
	
	public String getServicePathKey()
	{
		return servicePathKey;
	}

	public void setServicePathKey(String servicePathKey)
	{
		this.servicePathKey = servicePathKey;
	}

	@Override
	public ServiceObject transform(String _msg) throws Exception 
	{
		HashMap<String, Object> requestMap = this.jsonMapper.readValue(_msg, this.mapType);
		
		//타입키 관련 클래스추가
		ServiceObject serviceObject = new ServiceObject((String)requestMap.get(this.servicePathKey));
		serviceObject.setRequest(requestMap);
		
		return serviceObject;
	}
}
