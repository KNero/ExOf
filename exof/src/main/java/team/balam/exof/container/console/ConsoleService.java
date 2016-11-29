package team.balam.exof.container.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.balam.exof.container.SchedulerManager;
import team.balam.exof.module.service.ServiceProvider;

public class ConsoleService
{
	public Object getServiceList()
	{
		Map<String, HashMap<String, String>> result = ServiceProvider.getInstance().getAllServiceInfo();
		if(result.size() == 0)
		{
			return Command.NO_DATA_RESPONSE;
		}
		else
		{
			return result;
		}
	}
	
	public Object getScheduleList()
	{
		List<String> list = SchedulerManager.getInstance().getScheduleList();
		if(list.size() == 0)
		{
			return Command.NO_DATA_RESPONSE;
		}
		else
		{
			Map<String, Object> result = new HashMap<>();
			result.put("list", list);
			
			return result;
		}
	}
}