package balam.exof.scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.environment.EnvKey;
import balam.exof.util.CircularList;
import balam.exof.util.CollectionUtil;

public abstract class SchedulerJob implements Job, Observer
{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static String name;
	private static Boolean isDuplicateExecution;
	private static AtomicBoolean isExecute = new AtomicBoolean(false);
	protected static CircularList<Parameter> paramList;
	
	abstract void execute(Parameter _param) throws SchedulerExecuteException;
	
	private void _init(JobExecutionContext _arg)
	{
		if(name == null)
		{
			name = (String)_arg.getMergedJobDataMap().remove("name");
			isDuplicateExecution = (Boolean)_arg.getMergedJobDataMap().remove(EnvKey.Service.DUPLICATE);
			
			@SuppressWarnings("unchecked")
			List<Map<String, ?>> paramGroup = (List<Map<String, ?>>)_arg.getMergedJobDataMap().remove(EnvKey.Service.PARAM_GROUP);
			if(paramGroup != null)
			{
				List<Parameter> list = new LinkedList<Parameter>();
				CollectionUtil.doIterator(paramGroup, _param -> {
					Parameter p = new Parameter();
					list.add(p);
					
					CollectionUtil.doIterator(_param.keySet(), _key -> {
						p.set(_key, _param.get(_key));
					});
				});
				
				paramList = new CircularList<Parameter>(list);
			}
		}
	}

	@Override
	final public void execute(JobExecutionContext _arg) throws JobExecutionException 
	{
		this._init(_arg);
		
		if(! (isDuplicateExecution || isExecute.compareAndSet(false, true))) return;

		Parameter param = paramList.next();
		
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
			isExecute.set(false);
		}
	}
	
	@Override
	final public void update(Observable _o, Object _arg)
	{
		
	}
}
