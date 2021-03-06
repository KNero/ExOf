package team.balam.exof.container.console;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Command
{
	public interface Key
	{
		String RESULT = "result";
		String CLASS = "class";
		String NAME = "name";
		String ID = "id";
		String VALUE = "value";
		String DESCRIPTION = "des";
		String SERVICE_PATH = "servicePath";
		String VARIABLE_NAME = "variableName";
		String VARIABLE_VALUE = "variableValue";
		String PORT = "port";
		String ATTRIBUTE = "attribute";
		String CHILD_ATTRIBUTE = "child_attribute";
		String CRON = "cron";
		String TEXT = "text";
	}
	
	public static final String NO_DATA_RESPONSE = makeSimpleResult("No data.");
	public static final String SUCCESS_RESPONSE = makeSimpleResult("Success");
	
	public static String makeSimpleResult(String _value) {
		return "{\"" + Key.RESULT + "\":\"" + _value + "\"}";
	}
	
	@Expose
	private String type;
	
	@Expose
	private Map<String, Object> parameter = new HashMap<>();
	
	public Command(ServiceList _service)
	{
		this.type = _service.value();
	}
	
	public String getType()
	{
		return type;
	}
	
	public Map<String, Object> getParameter()
	{
		return parameter;
	}

	public void setParameter(Map<String, Object> parameter)
	{
		this.parameter = parameter;
	}
	
	public void addParameter(String _key, String _param)
	{
		this.parameter.put(_key, _param);
	}

	public String toJson()
	{
		Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC)
				.excludeFieldsWithoutExposeAnnotation().create();
		
		return gson.toJson(this);
	}
}
