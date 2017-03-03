package team.balam.exof.module.listener.handler.transform;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.service.ServiceObject;

public class JsonTransform implements ServiceObjectTransform<String>
{
	protected ObjectMapper jsonMapper = new ObjectMapper();
	protected TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>(){};
	
	/**
	 * servicePathKey를 설정하여 map에서 service path를 꺼낼 수 있다.
	 */
	protected String servicePathKey = "servicePath";
	
	@Override
	public void init(PortInfo _portInfo) throws Exception
	{
		
	}
	
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
		
		return this.transform(requestMap);
	}
	
	public ServiceObject transform(Map<String, Object> _reqMap)
	{
		//타입키 관련 클래스추가
		ServiceObject serviceObject = new ServiceObject((String)_reqMap.get(this.servicePathKey));
		serviceObject.setRequest(_reqMap);
		
		return serviceObject;
	}
}
