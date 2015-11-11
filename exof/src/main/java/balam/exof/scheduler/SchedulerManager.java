package balam.exof.scheduler;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
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
import balam.exof.environment.SystemSetting;
import balam.exof.util.CollectionUtil;

public class SchedulerManager implements Container, Observer
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Scheduler scheduler;
//	private boolean isAutoReload;
	
//	private Map<String, JobKey> jobKeyMap = new HashMap<String, JobKey>();
	
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
		List<SchedulerInfo> infoList = SystemSetting.getInstance()
				.getListAndRemove(EnvKey.PreFix.SERVICE, EnvKey.Service.SCHEDULE);
		
		if(infoList.size() > 0)
		{
			Properties pro = (Properties)SystemSetting.getInstance()
					.getAndRemove(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.SCHEDULER);
			SchedulerFactory factory = new StdSchedulerFactory(pro);
			this.scheduler = factory.getScheduler();
			
			CollectionUtil.doIterator(infoList, _info -> {
				try
				{
					JobDetail jd = JobBuilder.newJob(SchedulerJob.class).build();
					jd.getJobDataMap().put("info", _info);
					
					CronExpression ce = new CronExpression(_info.getCronExpression());
					CronTrigger t = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(ce)).build();
					
					this.scheduler.scheduleJob(jd, t);
					
					if(this.logger.isInfoEnabled())
					{
						this.logger.info("Loading schedule is successed.\n" + _info.toString());
					}
				}
				catch(Exception e)
				{
					this.logger.error("Loading schedule is failed.[{}]", _info.getServicePath(), e);
				}
			});
			
			this.scheduler.start();
		}
//		
//		if(infoList != null)
//		{
//			Properties pro = (Properties)SystemSetting.getInstance().getAndRemove(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.SCHEDULER);
//			SchedulerFactory factory = new StdSchedulerFactory(pro);
//			this.scheduler = factory.getScheduler();
//			
//			CollectionUtil.doIterator(infoList, _info -> {
//				try 
//				{
//					Class<SchedulerJob> jobClass = (Class<SchedulerJob>)Class.forName(_info.getClassName());
//					JobDetail jd = JobBuilder.newJob(jobClass).build();
//					jd.getJobDataMap().put(EnvKey.Service.NAME, _info.getName());
//					jd.getJobDataMap().put(EnvKey.Service.DUPLICATE, _info.isDuplicateExecution());
//					jd.getJobDataMap().put("isRunning", new AtomicBoolean(false));
//					
//					List<Map<String, ?>> paramGroup = (List<Map<String, ?>>)_info.getParamList();
//					CircularList<Parameter> paramList = new CircularList<>(this._makeParamGroupToList(paramGroup));
//					
//					jd.getJobDataMap().put(EnvKey.Service.PARAM_GROUP, paramList);
//					
//					//파라미터 변경시 해당 job을 찾기위해서 사용.
//					this.jobKeyMap.put(_info.getName(), jd.getKey());
//					
//					CronExpression ce = new CronExpression(_info.getCronExpression());
//					CronTrigger t = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(ce)).build();
//					
//					this.scheduler.scheduleJob(jd, t);
//					
//					if(this.logger.isDebugEnabled())
//					{
//						this.logger.debug("Loading schedule is successed.\n" + _info.toString());
//					}
//				} 
//				catch(Exception e) 
//				{
//					this.logger.error("Loading schedule is failed.[{}]", _info.getName(), e);
//				}
//			});
//			
//			this.scheduler.start();
//			
//			if(this.logger.isInfoEnabled())
//			{
//				this.logger.info("Scheduler is Loaded. Schedule Count : {}", infoList.size());
//			}
//			
//			String isAutoReload = pro.getProperty(EnvKey.Framework.AUTORELOAD_PARAM, "false");
//			this.isAutoReload = "true".equals(isAutoReload);
//		}
	}
	
//	private List<Parameter> _makeParamGroupToList(List<Map<String, ?>> _paramGroup)
//	{
//		List<Parameter> list = new LinkedList<>();
//		CollectionUtil.doIterator(_paramGroup, _param -> {
//			Parameter p = new Parameter();
//			list.add(p);
//			
//			CollectionUtil.doIterator(_param.keySet(), _key -> {
//				p.set(_key, _param.get(_key));
//			});
//		});
//		
//		return list;
//	}

	@Override
	public void stop() throws Exception
	{
		if(this.scheduler != null) this.scheduler.shutdown();
	}

	@Override
	public void update(Observable o, Object arg) 
	{
//		if(this.isAutoReload)
//		{
//			Map<String, ?> schedule = (Map<String, ?>)((Map<String, ?>)arg).get(EnvKey.Service.SCHEDULE);
//			if(schedule != null)
//			{
//				CollectionUtil.doIterator(schedule.keySet(), _key -> {
//					Map<String, ?> info = (Map<String, ?>)schedule.get(_key);
//					JobKey jobkey = this.jobKeyMap.get(_key);
//					try 
//					{
//						List<Map<String, ?>> paramListMap = new LinkedList<>();
//						
//						List<Map<String, ?>> paramGroup = (List<Map<String, ?>>)info.get(EnvKey.Service.PARAM_GROUP);
//						CollectionUtil.doIterator(paramGroup, _param -> {
//							paramListMap.add((Map<String, ?>)_param.get(EnvKey.Service.PARAM));
//						});
//						
//						List<Parameter> paramList = this._makeParamGroupToList(paramListMap);
//						
//						JobDetail job = this.scheduler.getJobDetail(jobkey);
//						CircularList<Parameter> jobParam = (CircularList<Parameter>)job.getJobDataMap().get(EnvKey.Service.PARAM_GROUP);
//						jobParam.set(paramList);
//					} 
//					catch(SchedulerException e) 
//					{
//						this.logger.error("Can not get scheduler job.", e);
//					}
//				});
//			}
//		}
	}
}
