package team.balam.exof.container;

import java.util.ArrayList;
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
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.Container;
import team.balam.exof.container.scheduler.ExecutionContext;
import team.balam.exof.container.scheduler.PauseAwareCronTrigger;
import team.balam.exof.container.scheduler.SchedulerAlreadyExists;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.vo.SchedulerInfo;
import team.balam.exof.container.scheduler.SchedulerJob;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;

public class SchedulerManager implements Container, Observer
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Scheduler scheduler;
	private boolean isAutoReload;

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
	public void start() throws Exception {
		Boolean isAutoReload = SystemSetting.getInstance().getFramework(EnvKey.Framework.AUTORELOAD_SCHEDULER);
		if (isAutoReload != null) {
			this.isAutoReload = isAutoReload;
		}

		List<SchedulerInfo> infoList = ServiceInfoDao.selectScheduler();
		if (infoList.size() > 0) {
			Properties pro = SystemSetting.getInstance().getFramework(EnvKey.Framework.SCHEDULER);
			SchedulerFactory factory = new StdSchedulerFactory(pro);
			this.scheduler = factory.getScheduler();

			infoList.forEach(this::_loadScheduler);

			if (this.logger.isInfoEnabled()) {
				this.logger.info("Scheduler is Loaded. Schedule Count : {}", infoList.size());
			}
		}
	}
	
	private void _loadScheduler(SchedulerInfo _info) {
		try {
			if (this.jobKeyMap.containsKey(_info.getId())) {
				throw new SchedulerAlreadyExists(_info.getId());
			}

			JobDetail jd = JobBuilder.newJob(SchedulerJob.class).build();
			jd.getJobDataMap().put("info", _info);

			CronExpression ce = new CronExpression(_info.getCronExpression());
			PauseAwareCronTrigger t = new PauseAwareCronTrigger(ce);

			this.scheduler.scheduleJob(jd, t);
			this.jobKeyMap.put(_info.getId(), jd.getKey());

			if (!_info.isUse()) {
				this.scheduler.pauseJob(jd.getKey());
			}

			if (this.logger.isInfoEnabled()) {
				this.logger.info("Loading schedule [{}]", _info.toString());
			}
		} catch (Exception e) {
			this.logger.error("Loading schedule is failed.[{}]", _info.getServicePath(), e);
		}
	}
	
	public void executeInitTimeAndStart() {
		List<SchedulerInfo> infoList = SystemSetting.getInstance().getList(EnvKey.FileName.SERVICE, EnvKey.Service.SCHEDULER);
		infoList.forEach(info -> {
			if (this.jobKeyMap.containsKey(info.getId())) {
				if (info.isUse() && info.isInitExecution()) {
					ExecutionContext exeCtx = new ExecutionContext(info);
					SchedulerJob job = new SchedulerJob();
					
					try {
						job.execute(exeCtx);
					} catch (Exception e) {
						this.logger.error("Failed to execute scheduler in init time. ServicePath : {}", info.getServicePath(), e);
					}
				}
				
			}
		});
		
		try {
			this.scheduler.start();
		} catch (SchedulerException e) {
			this.logger.error("Scheduler start error.", e);
		}
	}
	
	@Override
	public void stop() throws Exception
	{
		if(this.scheduler != null) this.scheduler.shutdown();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!this.isAutoReload) return;

		List<SchedulerInfo> infoList = ServiceInfoDao.selectScheduler();
		infoList.forEach(_info -> {
			JobKey jobkey = this.jobKeyMap.get(_info.getId());
			if (jobkey != null) {
				try {
					if (!_info.isUse()) {
						this.scheduler.pauseJob(jobkey);
					} else {
						this.scheduler.resumeJob(jobkey);
					}

					this.logger.warn("Complete reloading schedulerInfo. [{}]", _info.getServicePath());
				} catch (SchedulerException e) {
					this.logger.error("Can not update scheduler. [{}]", _info.toString(), e);
				}
			}
		});
	}
	
	public List<String> getScheduleList()
	{
		ArrayList<String> list = new ArrayList<>();
		
		this.jobKeyMap.forEach((_id, _jobKey) -> {
			try
			{
				JobDataMap jobDataMap = this.scheduler.getJobDetail(_jobKey).getJobDataMap();
				SchedulerInfo info = (SchedulerInfo)jobDataMap.get("info");
				
				
				list.add("ID:" + info.getId() + ", service path:" + info.getServicePath() 
						+ ", cron:" + info.getCronExpression() + ", use:" + (info.isUse() ? "yes" : "no")
						+ ", duplicateExecution:" + (info.isDuplicateExecution() ? "yes" : "no"));
			}
			catch(Exception e)
			{
				String error = "Can not get schedule info. ID[" + _id + "]";
				
				this.logger.error(error, e);
				
				list.add(error);
			}
		});
		
		return list;
	}
}
