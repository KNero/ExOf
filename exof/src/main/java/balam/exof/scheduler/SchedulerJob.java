package balam.exof.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.environment.EnvKey;
import balam.exof.util.CircularList;

public abstract class SchedulerJob implements Job
{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	abstract void execute(Parameter _param) throws SchedulerExecuteException;
	
	@Override
	final public void execute(JobExecutionContext _arg) throws JobExecutionException 
	{
		JobDataMap jobData = _arg.getMergedJobDataMap();
		
		AtomicBoolean isRunning = (AtomicBoolean)jobData.get("isRunning");
		Boolean isDuplicateExecution = (Boolean)jobData.get(EnvKey.Service.DUPLICATE);
		
		if(! (isDuplicateExecution || isRunning.compareAndSet(false, true))) 
		{
			if(this.logger.isInfoEnabled())
			{
				this.logger.info("Skip schedule because the previous job is not finished.");
			}
			
			return;
		}
		
		@SuppressWarnings("unchecked")
		CircularList<Parameter> paramGroup = (CircularList<Parameter>)jobData.get(EnvKey.Service.PARAM_GROUP);
		Parameter param = paramGroup.next();

		try
		{
			this.execute(param);
		}
		catch(SchedulerExecuteException e)
		{
			this.logger.error("An error occurs during run. Parameter : {}", param.toString(), e);
		}
		finally
		{
			isRunning.set(false);
		}
	}
}
