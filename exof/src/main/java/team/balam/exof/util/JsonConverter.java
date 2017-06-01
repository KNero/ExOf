package team.balam.exof.util;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JsonConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	
	public final String toJson() {
		StringWriter out = new StringWriter();
		
		try {
			JSON_MAPPER.writeValue(out, this);
		} catch (IOException e) {
			LOGGER.error("Fail to convert json string.", e);
			
			out.write("{\"Error\": \"Fail to get table info. => " + e.getMessage() + "\"");
		}
		
		return out.toString(); 
	}
}
