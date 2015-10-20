package balam.exof;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.environment.EnvKey;
import balam.exof.environment.Setting;
import balam.exof.environment.SystemSetting;
import balam.exof.scheduler.SchedulerManager;
import balam.exof.util.CollectionUtil;

/**
 * 컨테이너의 구동을 담당한다.
 * @author kwonsm
 *
 */
public class Operator 
{
	private static Logger logger = LoggerFactory.getLogger(Operator.class);
	private static List<Container> containerList = new LinkedList<Container>();
	
	@SuppressWarnings("unchecked")
	public static void init()
	{
		containerList.add(new Framework());
		containerList.add(SchedulerManager.getInstance());
		
		List<String> extraContainerList = (List<String>)SystemSetting.getInstance()
				.getAndRemove(Setting.PreFix.FRAMEWORK, EnvKey.Framework.CONTAINER);
		CollectionUtil.doIterator(extraContainerList, _containerClass -> {
			try 
			{
				Container container = (Container)Class.forName(_containerClass).newInstance();
				containerList.add(container);
			} 
			catch (Exception e) 
			{
				logger.error("Container[{}] can not create.", _containerClass, e);
			}
		});
	}
	
	public static void start()
	{
		CollectionUtil.doIterator(containerList, _container -> {
			try
			{
				_container.start();
			}
			catch(Exception e) { logger.error("Container[{}] can not start.", _container.getName(), e); }
		});
	}
	
	public static void stop()
	{
		CollectionUtil.doIterator(containerList, _container -> {
			try
			{
				_container.stop();
			}
			catch(Exception e) { logger.error("Container[{}] can not stop.", _container.getName(), e); }
		});
	}
}
