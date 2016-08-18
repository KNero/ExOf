package team.balam.exof.container.console;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import team.balam.exof.ConstantKey;

public class Command
{
	public static final Map<String, String> NO_DATA_RESPONSE = new HashMap<>();
	
	static
	{
		NO_DATA_RESPONSE.put(ConstantKey.RESULT_KEY, "No data.");
	}
	
	@Expose
	private String type;
	
	@Expose
	private List<String> parameter = new LinkedList<>();
	
	public Command(String _type)
	{
		this.type = _type;
	}
	
	public String getType()
	{
		return type;
	}
	
	public List<String> getParameter()
	{
		return parameter;
	}

	public void setParameter(List<String> parameter)
	{
		this.parameter = parameter;
	}
	
	public void addParameter(String _param)
	{
		this.parameter.add(_param);
	}

	public String toJson()
	{
		Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC)
				.excludeFieldsWithoutExposeAnnotation().create();
		
		return gson.toJson(this) + "\0";
	}
	
	public interface Type
	{
		String SHOW_SERVICE_LIST = "showServiceList";
		String SHOW_SCHEDULE_LIST = "showScheduleList";
	}
}
