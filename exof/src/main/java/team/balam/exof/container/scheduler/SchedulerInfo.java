package team.balam.exof.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * ServiceLoader에서 SchedulerManager로 
 * 스케쥴에 대한 정보를 넘기기 위해서 사용한다.
 * @author kwonsm
 *
 */
public class SchedulerInfo 
{
	private String id;
	private String servicePath;
	private String cronExpression;
	private boolean isDuplicateExecution;
	private boolean isUse;
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getServicePath()
	{
		return servicePath;
	}
	
	public void setServicePath(String servicePath)
	{
		this.servicePath = servicePath;
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

	public AtomicBoolean getIsRunning()
	{
		return isRunning;
	}
	
	public boolean isUse()
	{
		return isUse;
	}

	public void setUse(boolean isUse)
	{
		this.isUse = isUse;
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append("ID : ").append(this.id);
		str.append(" , ServicePath : ").append(this.servicePath);
		str.append(" , cron : ").append(this.cronExpression);
		str.append(" , DuplicateExecution : ").append(this.isDuplicateExecution);
		
		return str.toString();
	}
}
