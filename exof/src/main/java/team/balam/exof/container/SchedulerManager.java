package team.balam.exof.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import org.quartz.CronExpression;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.Container;
import team.balam.exof.container.scheduler.PauseAwareCronTrigger;
import team.balam.exof.container.scheduler.SchedulerAlreadyExists;
import team.balam.exof.container.scheduler.SchedulerInfo;
import team.balam.exof.container.scheduler.SchedulerJob;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;

public class SchedulerManager implements Container, Observer
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Scheduler scheduler;
	private boolean isAutoReload;
	private long updateVariableTime;
	
	private Map<String, JobKey> jobKeyMap = new HashMap<>();
	
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
	
	@Override
	public void start() throws Exception
	{
		this.isAutoReload = (Boolean)SystemSetting.getInstance()
				.get(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.AUTORELOAD_SCHEDULER);
		
		List<SchedulerInfo> infoList = SystemSetting.getInstance()
				.getListAndRemove(EnvKey.PreFix.SERVICE, EnvKey.Service.SCHEDULE);
		
		if(infoList.size() > 0)
		{
			Properties pro = (Properties)SystemSetting.getInstance()
					.getAndRemove(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.SCHEDULER);
			SchedulerFactory factory = new StdSchedulerFactory(pro);
			this.scheduler = factory.getScheduler();
			
			infoList.forEach(_info -> {
				try
				{
					if(this.jobKeyMap.containsKey(_info.getId()))
					{
						throw new SchedulerAlreadyExists(_info.getId());
					}
					
					JobDetail jd = JobBuilder.newJob(SchedulerJob.class).build();
					jd.getJobDataMap().put("info", _info);
					
					CronExpression ce = new CronExpression(_info.getCronExpression());
					PauseAwareCronTrigger t = new PauseAwareCronTrigger(ce);
					
					this.scheduler.scheduleJob(jd, t);
					
					this.jobKeyMap.put(_info.getId(), jd.getKey());
					
					if(! _info.isUse()) this.scheduler.pauseJob(jd.getKey());
					
					if(this.logger.isInfoEnabled())
					{
						this.logger.info("Loading schedule [{}]", _info.toString());
					}
				}
				catch(Exception e)
				{
					this.logger.error("Loading schedule is failed.[{}]", _info.getServicePath(), e);
				}
			});
			
			if(this.logger.isInfoEnabled())
			{
				this.logger.info("Scheduler is Loaded. Schedule Count : {}", infoList.size());
			}
			
			this.scheduler.start();
		}
	}
	
	@Override
	public void stop() throws Exception
	{
		if(this.scheduler != null) this.scheduler.shutdown();
	}

	@Override
	public void update(Observable o, Object arg) 
	{
		List<SchedulerInfo> infoList = SystemSetting.getInstance()
				.getListAndRemove(EnvKey.PreFix.SERVICE, EnvKey.Service.SCHEDULE);
		
		if(! this.isAutoReload) return;
		
		if(System.currentTimeMillis() - this.updateVariableTime > 5000)
		{
			this.updateVariableTime = System.currentTimeMillis();
			
			infoList.forEach(_info -> {
				JobKey jobkey = this.jobKeyMap.get(_info.getId());
				
				try
				{
					JobDataMap dataMap = this.scheduler.getJobDetail(jobkey).getJobDataMap();
					SchedulerInfo realInfo = (SchedulerInfo)dataMap.get("info");
					
					if(_info.isUse() != realInfo.isUse())
					{
						realInfo.setUse(_info.isUse());
						
						if(! _info.isUse()) this.scheduler.pauseJob(jobkey);
						else this.scheduler.resumeJob(jobkey);
						
						this.logger.warn("Complete reloading schedulerInfo. [{}]", _info.getServicePath());
					}
				} 
				catch(Exception e)
				{
					this.logger.error("Can not update scheduler. [{}]", _info.toString(), e);
				}
			});
		}
	}
}
