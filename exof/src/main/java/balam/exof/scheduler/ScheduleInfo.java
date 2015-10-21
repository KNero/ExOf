package balam.exof.scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import balam.exof.util.CollectionUtil;

public class ScheduleInfo 
{
	private String name;
	private String className;
	private String cronExpression;
	private boolean isDuplicateExecution;
	private List<Map<String, ?>> paramList = new LinkedList<>();
	
	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getClassName() 
	{
		return className;
	}
	
	public void setClassName(String className) 
	{
		this.className = className;
	}
	
	public String getCronExpression() 
	{
		return cronExpression;
	}
	
	public void setCronExpression(String cronExpression) 
	{
		this.cronExpression = cronExpression;
	}
	
	public boolean isDuplicateExecution() 
	{
		return isDuplicateExecution;
	}
	
	public void setDuplicateExecution(boolean isDuplicateExecution) 
	{
		this.isDuplicateExecution = isDuplicateExecution;
	}
	
	public List<Map<String, ?>> getParamList() 
	{
		return paramList;
	}
	
	public void setParamList(List<Map<String, ?>> paramList) 
	{
		this.paramList = paramList;
	}
	
	public void addParam(Map<String, ?> _param)
	{
		this.paramList.add(_param);
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append("Name : ").append(this.name).append("\n");
		str.append("Class : ").append(this.className).append("\n");
		str.append("CronExpression : ").append(this.cronExpression).append("\n");
		str.append("DuplicateExecution : ").append(this.isDuplicateExecution).append("\n");
		
		CollectionUtil.doIterator(this.paramList, _param -> {
			str.append("Parameter : ").append(_param.toString()).append("\n");
		});
		
		return str.toString();
	}
}
