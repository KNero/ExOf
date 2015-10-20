package balam.exof.scheduler;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SchedulerJob implements Job
{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected ParamList paramList;
	
	final public void init(List<?> _paramList) throws Exception
	{
		
	}
	
	abstract void reload() throws ReloadException;
	
	abstract void execute(Parameter _param) throws SchedulerExecuteException;

	@Override
	final public void execute(JobExecutionContext arg0) throws JobExecutionException 
	{
		try
		{
			this.execute(this.paramList.next());
		}
		catch(SchedulerExecuteException e)
		{
			
		}
	}
	
	/**
	 * Parameter를 원형 LinkedList의 형태로 저장하는 클래스
	 * @author kwonsm
	 *	
	 */
	private class ParamList
	{
		private ParamListObject current;
		
		private Parameter next()
		{
			Parameter param = this.current.param;
			this.current = this.current.next;
			
			return param;
		}
	}
	
	private class ParamListObject
	{
		private Parameter param;
		private ParamListObject next;
	}
}
