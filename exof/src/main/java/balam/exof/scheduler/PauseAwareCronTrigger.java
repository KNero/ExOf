package balam.exof.scheduler;

import java.util.Date;
import java.util.UUID;

import org.quartz.CronExpression;
import org.quartz.impl.triggers.CronTriggerImpl;

public class PauseAwareCronTrigger extends CronTriggerImpl
{
	private static final long serialVersionUID = -2609414523458705574L;
	
	public PauseAwareCronTrigger(CronExpression _ce)
	{
		super();
		
		this.setCronExpression(_ce);
		this.setName(UUID.randomUUID().toString());
	}
	
	@Override
	public Date getNextFireTime()
	{
		Date nextFireTime = super.getNextFireTime();
		if(nextFireTime.getTime() < System.currentTimeMillis())
		{
			// next fire time after now
			nextFireTime = super.getFireTimeAfter(null);
			super.setNextFireTime(nextFireTime);
		}
		
		return nextFireTime;
	}
}
