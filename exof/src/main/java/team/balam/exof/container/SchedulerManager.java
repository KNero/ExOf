package team.balam.exof.container;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.Container;
import team.balam.exof.container.scheduler.ExecutionContext;
import team.balam.exof.container.scheduler.PauseAwareCronTrigger;
import team.balam.exof.container.scheduler.SchedulerAlreadyExists;
import team.balam.exof.container.scheduler.SchedulerJob;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.environment.vo.SchedulerInfo;

import java.text.ParseException;
import java.util.*;

public class SchedulerManager implements Container, Observer
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Scheduler scheduler;
	private boolean isAutoReload;

	private Map<String, JobKey> jobKeyMap = new HashMap<>();
	private Map<String, TriggerKey> triggerKeyMap = new HashMap<>();
	
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
		Boolean isAutoReload = SystemSetting.getFramework(EnvKey.Framework.AUTORELOAD_SCHEDULER);
		if (isAutoReload != null) {
			this.isAutoReload = isAutoReload;
		}

		List<SchedulerInfo> infoList = ServiceInfoDao.selectScheduler();
		if (infoList.size() > 0) {
			Properties pro = SystemSetting.getFramework(EnvKey.Framework.SCHEDULER);

			SchedulerFactory factory;
			if (pro != null) {
				factory = new StdSchedulerFactory(pro);
			} else {
				factory = new StdSchedulerFactory();
			}

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

			TriggerKey triggerKey = new TriggerKey(_info.getId());
			this.triggerKeyMap.put(_info.getId(), triggerKey);

			CronExpression ce = new CronExpression(_info.getCronExpression());
			PauseAwareCronTrigger t = new PauseAwareCronTrigger(ce);
			t.setKey(triggerKey);

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
		if (this.scheduler != null) {
			boolean isNotStarted = false;

			try {
				isNotStarted = !this.scheduler.isStarted();
			} catch (SchedulerException e) {
			}

			if (isNotStarted) {
				List<SchedulerInfo> infoList = ServiceInfoDao.selectScheduler();
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
		}
	}
	
	@Override
	public void stop() throws Exception {
		if(this.scheduler != null) {
			this.scheduler.shutdown();
		}

		this.jobKeyMap = new HashMap<>();
		this.triggerKeyMap = new HashMap<>();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!this.isAutoReload) return;

		String schedulerId = (String) arg;

		SchedulerInfo newInfo = ServiceInfoDao.selectScheduler(schedulerId);
		if (!newInfo.isNull()) {
			JobKey jobkey = this.jobKeyMap.get(schedulerId);
			if (jobkey != null) {
				try {
					JobDetail jobDetail = this.scheduler.getJobDetail(jobkey);
					SchedulerInfo currentInfo = (SchedulerInfo) jobDetail.getJobDataMap().get("info");
					currentInfo.setUse(newInfo.isUse());

					this.updateCronExpression(schedulerId, newInfo.getCronExpression());

					this.logger.warn("Complete reloading schedulerInfo. [{}]", newInfo.getServicePath());
				} catch (SchedulerException e) {
					this.logger.error("Can not update scheduler. [{}]", newInfo.toString(), e);
				}
			}
		}
	}

	private void updateCronExpression(String _schedulerId, String _cronExpression) throws SchedulerException {
		try {
			TriggerKey newTrigger = new TriggerKey(_schedulerId);
			this.triggerKeyMap.put(_schedulerId, newTrigger);

			CronExpression newCron = new CronExpression(_cronExpression);
			PauseAwareCronTrigger trigger = new PauseAwareCronTrigger(newCron);
			trigger.setKey(newTrigger);

			this.scheduler.rescheduleJob(newTrigger, trigger);
		} catch (ParseException e) {
			this.logger.error("Fail to update cron expression. " + _cronExpression, e);
		}
	}
}
