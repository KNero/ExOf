package team.balam.exof.module.service;

import java.util.HashMap;
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
	private Map<String, ServiceVariable> variableMap = new HashMap<>();
	
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
	
	public void setVariable(String _serviceName, ServiceVariable _variable)
	{
		this.variableMap.put(_serviceName, _variable);
	}
	
	public ServiceVariable getVariable(String _serviceName)
	{
		return this.variableMap.get(_serviceName);
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

			ServiceVariable variable = this.variableMap.get(serviceName);
			str.append("\n-- Service Variable\n").append(variable.toString());
		}
		
		return str.toString();
	}
}
