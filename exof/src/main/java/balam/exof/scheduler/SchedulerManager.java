package balam.exof.scheduler;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.Container;
import balam.exof.environment.EnvKey;
import balam.exof.environment.Setting;
import balam.exof.environment.SystemSetting;

public class SchedulerManager implements Container
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
	
	@Override
	public void start() throws Exception
	{
		Properties pro = (Properties)SystemSetting.getInstance().getAndRemove(Setting.PreFix.FRAMEWORK, EnvKey.Framework.SCHEDULER);
		SchedulerFactory factory = new StdSchedulerFactory(pro);
		this.scheduler = factory.getScheduler();
		
		this.scheduler.start();
	}

	@Override
	public void stop() throws Exception
	{
		this.scheduler.shutdown();
	}
}
