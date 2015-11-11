package balam.exof.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
		return this.variableMap.get(_serviceName);
	}
}