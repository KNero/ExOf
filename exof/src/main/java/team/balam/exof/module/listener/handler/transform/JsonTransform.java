package team.balam.exof.module.listener.handler.transform;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import team.balam.exof.module.service.ServiceObject;

public class JsonTransform implements ServiceObjectTransform<String>
{
	protected ObjectMapper jsonMapper = new ObjectMapper();
	protected TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>(){};
	
	/**
	 * servicePathKey를 설정하여 map에서 service path를 꺼낼 수 있다.
	 */
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
	public ServiceObject transform(String _msg) throws Exception {
		Map<String, Object> requestMap = this.toMap(_msg);

		ServiceObject serviceObject = new ServiceObject((String) requestMap.get(this.servicePathKey));
		serviceObject.setRequest(requestMap);

		return serviceObject;
	}
	
	public Map<String, Object> toMap(String _msg) throws Exception {
		return this.jsonMapper.readValue(_msg, this.mapType);
	}
	
	public String toString(Object _value) throws Exception {
		StringWriter writer = new StringWriter();
		this.jsonMapper.writeValue(writer, _value);
		return writer.toString();
	}
}
