package balam.exof.scheduler;

import java.util.List;
import java.util.Observable;
import java.util.Properties;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.Container;
import balam.exof.environment.EnvKey;
import balam.exof.environment.Setting;
import balam.exof.environment.SystemSetting;
import balam.exof.util.CollectionUtil;

public class SchedulerManager extends Observable implements Container
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Scheduler scheduler;
	
	private static SchedulerManager self = new SchedulerManager();
	
	private SchedulerManager() {}
	
	public static SchedulerManager getInstance()
	{
		return self;
	}

	@Override
	public String getName() 
	{
		return "SchedulerManager";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start() throws Exception
	{
		Properties pro = (Properties)SystemSetting.getInstance().getAndRemove(Setting.PreFix.FRAMEWORK, EnvKey.Framework.SCHEDULER);
		SchedulerFactory factory = new StdSchedulerFactory(pro);
		this.scheduler = factory.getScheduler();
		
		List<ScheduleInfo> infoList = (List<ScheduleInfo>)SystemSetting.getInstance()
				.get(Setting.PreFix.SERVICE, EnvKey.Service.SCHEDULE);
		
		CollectionUtil.doIterator(infoList, _info -> {
			try 
			{
				Class<SchedulerJob> jobClass = (Class<SchedulerJob>)Class.forName(_info.getClassName());
				JobDetail jd = JobBuilder.newJob(jobClass).build();
				jd.getJobDataMap().put("name", _info.getName());
				jd.getJobDataMap().put(EnvKey.Service.DUPLICATE, _info.isDuplicateExecution());
				jd.getJobDataMap().put(EnvKey.Service.PARAM_GROUP, _info.getParamList());
				
				CronExpression ce = new CronExpression(_info.getCronExpression());
				CronTrigger t = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(ce)).build();
				
				this.scheduler.scheduleJob( jd, t );
				
				if(this.logger.isInfoEnabled())
				{
					this.logger.info("Loading schedule is successed.\n" + _info.toString());
				}
			} 
			catch(Exception e) 
			{
				this.logger.error("Loading schedule is failed.[{}]", _info.getName(), e);
			}
		});
		
		this.scheduler.start();
	}

	@Override
	public void stop() throws Exception
	{
		this.scheduler.shutdown();
	}
}
