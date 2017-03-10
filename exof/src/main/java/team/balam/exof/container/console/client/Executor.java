package team.balam.exof.container.console.client;

import java.util.HashMap;
import java.util.Map;

public class Executor 
{
	private static Map<String, Runnable> executorList = new HashMap<String, Runnable>();
	
	private static InfoGetter getter = new InfoGetter();
	
	static
	{
		executorList.put(Menu.Execute.GET_SERVICE_LIST, () -> {
			getter.getServiceList();
		});
		
		executorList.put(Menu.Execute.GET_SCHEDULE_LIST, () -> {
			getter.getScheduleList();
		});
	}
	
	public static void execute(String _cmd)
	{
		if(executorList.containsKey(_cmd))
		{
			executorList.get(_cmd).run();
		}
	}
}
