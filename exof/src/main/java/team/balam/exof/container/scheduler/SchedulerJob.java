package team.balam.exof.container.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.environment.vo.SchedulerInfo;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceProvider;

public class SchedulerJob implements Job
{
	private static Logger logger = LoggerFactory.getLogger(SchedulerJob.class);
	
	@Override
	final public void execute(JobExecutionContext _arg) throws JobExecutionException 
	{
		JobDataMap jobData = _arg.getMergedJobDataMap();
		SchedulerInfo info = (SchedulerInfo)jobData.get("info");

		if (!info.isUse()) {
			return;
		}
		
		AtomicBoolean isRunning = info.getIsRunning();
		Boolean isDuplicateExecution = info.isDuplicateExecution();
		
		if(! (isDuplicateExecution || isRunning.compareAndSet(false, true))) 
		{
			if(logger.isInfoEnabled())
			{
				logger.info("Skip schedule because the previous job is not finished.");
			}
			
			return;
		}
		
		try
		{
			long start = System.currentTimeMillis();
			
			if(logger.isDebugEnabled())
			{
				logger.debug("Schedule[{}] is started.", info.getServicePath());
			}
			
			ServiceObject so = new ServiceObject(info.getServicePath());
			ServiceWrapper service = ServiceProvider.lookup(so);
			
			service.call(so);
			
			if(logger.isInfoEnabled())
			{
				long end = System.currentTimeMillis();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
				
				logger.info("Schedule[{}] Start:{} / End:{} / Elapsed:{}ms", info.getServicePath(), 
						format.format(new Date(start)), format.format(new Date(end)), (end - start));
			}
		}
		catch(Exception e)
		{
			logger.error("An error occurs during run. Service : {}", info.getServicePath(), e);
		}
		finally
		{
			isRunning.set(false);
		}
	}
}
