package team.balam.exof.module.listener.handler.transform;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.type.TypeReference;
import team.balam.exof.module.service.ServiceObject;

import java.io.IOException;
import java.io.StringWriter;

public abstract class AbstractJsonTransform<T> implements ServiceObjectTransform<String> {
	private ObjectMapper jsonMapper = new ObjectMapper();
	private Class<T> type;

	protected AbstractJsonTransform(Class<T> type) {
	    this.type = type;
    }

	@Override
	public ServiceObject transform(String _msg) throws IOException {
		T convertingResult = this.convert(_msg);

		ServiceObject serviceObject = new ServiceObject(this.getServicePath(convertingResult));
		serviceObject.setRequest(convertingResult);

		return serviceObject;
	}
	
	public T convert(String _msg) throws IOException {
		return this.jsonMapper.readValue(_msg, this.type);
	}
	
	public String toString(Object _value) throws IOException {
		StringWriter writer = new StringWriter();
		this.jsonMapper.writeValue(writer, _value);
		return writer.toString();
	}

	protected abstract String getServicePath(T t);
}
