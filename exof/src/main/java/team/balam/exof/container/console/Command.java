package team.balam.exof.container.console;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import team.balam.exof.ConstantKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Command
{
	public static String NO_DATA_RESPONSE = "{\"" + ConstantKey.RESULT_KEY + "\":\"No data\"}";
	
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
	}
}
