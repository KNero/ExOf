package team.balam.exof.container.scheduler;

import java.util.Date;

import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import team.balam.exof.environment.vo.SchedulerInfo;

public class ExecutionContext implements JobExecutionContext
{
	private JobDataMap dataMap;
	
	public ExecutionContext(SchedulerInfo _info)
	{
		this.dataMap = new JobDataMap();
		this.dataMap.put("info", _info);
	}
	
	@Override
	public JobDataMap getMergedJobDataMap() 
	{
		return this.dataMap;
	}
	
	@Override
	public void put(Object key, Object value) 
	{
		
	}

	@Override
	public Object get(Object key) 
	{
		return null;
	}
	
	@Override
	public Scheduler getScheduler() 
	{
		return null;
	}

	@Override
	public Trigger getTrigger() 
	{
		return null;
	}

	@Override
	public Calendar getCalendar() 
	{
		return null;
	}

	@Override
	public boolean isRecovering() 
	{
		return false;
	}

	@Override
	public TriggerKey getRecoveringTriggerKey() throws IllegalStateException 
	{
		return null;
	}

	@Override
	public int getRefireCount() 
	{
		return 0;
	}

	@Override
	public JobDetail getJobDetail() 
	{
		return null;
	}

	@Override
	public Job getJobInstance()
	{
		return null;
	}

	@Override
	public Date getFireTime() 
	{
		return null;
	}

	@Override
	public Date getScheduledFireTime() 
	{
		return null;
	}

	@Override
	public Date getPreviousFireTime() 
	{
		return null;
	}

	@Override
	public Date getNextFireTime() 
	{
		return null;
	}

	@Override
	public String getFireInstanceId() 
	{
		return null;
	}

	@Override
	public Object getResult() 
	{
		return null;
	}

	@Override
	public void setResult(Object result) 
	{
		
	}

	@Override
	public long getJobRunTime()
	{
		return 0;
	}
}
