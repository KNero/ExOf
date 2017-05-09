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
		String NO_DATA = "noData";
		String CLASS = "class";
	}
	
	public static final Map<String, String> NO_DATA_RESPONSE = new HashMap<>();
	
	static
	{
		NO_DATA_RESPONSE.put(Command.Key.NO_DATA, "No data.");
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
