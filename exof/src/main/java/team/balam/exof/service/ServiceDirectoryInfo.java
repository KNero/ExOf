package team.balam.exof.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loader에서 ServiceProvider로 정보를 전달하기 위해 사용
 * @author kwonsm
 *
 */
public class ServiceDirectoryInfo
{
	private String className;
	private String path;
	private Map<String, LinkedHashMap<String, String>> variableMap = new HashMap<>();
	
	public String getClassName()
	{
		return className;
	}
	
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public void setVariable(String _serviceName, LinkedHashMap<String, String> _variable)
	{
		this.variableMap.put(_serviceName, _variable);
	}
	
	public Map<String, String> getVariable(String _serviceName)
	{
		return this.variableMap.remove(_serviceName);
	}
	
	public int getServiceVariableSize()
	{
		return this.variableMap.size();
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append("Class : ").append(this.className);
		str.append("\nPath : ").append(this.path);
		str.append("\n- Variable List");
		
		for(String serviceName : this.variableMap.keySet())
		{
			str.append("\n-- Service Name : ").append(serviceName);

			Map<String, String> variable = this.variableMap.get(serviceName);
			for(String variableName : variable.keySet())
			{
				str.append("\n--- [").append(variableName).append("] = [");
				str.append(variable.get(variableName)).append("]");
			}
		}
		
		return str.toString();
	}
}
