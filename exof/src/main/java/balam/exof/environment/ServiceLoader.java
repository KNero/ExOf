package balam.exof.environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import balam.exof.scheduler.ScheduleInfo;
import balam.exof.util.CollectionUtil;

public class ServiceLoader implements Loader
{
	@SuppressWarnings("unchecked")
	@Override
	public void load(String _envPath) throws LoadEnvException 
	{
		FileInputStream serviceFile = null;
		
		try
		{
			serviceFile = new FileInputStream(_envPath + "/" + "service.yaml");
			
			Yaml yamlParser = new Yaml();
			Map<String, ?> root = (Map<String, ?>)yamlParser.load(serviceFile);
			
			Map<String, ?> schedule = (Map<String, ?>)root.get(EnvKey.Service.SCHEDULE);
			if(schedule != null)
			{
				List<ScheduleInfo> scheduleList = new LinkedList<>();
				SystemSetting.getInstance().set(EnvKey.PreFix.SERVICE, EnvKey.Service.SCHEDULE, scheduleList);
				
				CollectionUtil.doIterator(schedule.keySet(), _key -> {
					Map<String, ?> info = (Map<String, ?>)schedule.get(_key);
					
					ScheduleInfo scheduleInfo = new ScheduleInfo();
					scheduleInfo.setName(_key);
					scheduleInfo.setClassName((String)info.get(EnvKey.Service.CLASS));
					scheduleInfo.setCronExpression((String)info.get(EnvKey.Service.CLON));
					scheduleInfo.setDuplicateExecution((Boolean)info.get(EnvKey.Service.DUPLICATE));
					
					List<?> paramGroup = (List<?>)info.get(EnvKey.Service.PARAM_GROUP);
					if(paramGroup != null)
					{
						CollectionUtil.doIterator(paramGroup, _param -> {
							Map<String, ?> param = (Map<String, ?>)((Map<String, ?>)_param).get(EnvKey.Service.PARAM);
							scheduleInfo.addParam(param);
						});
					}
					
					scheduleList.add(scheduleInfo);
				});
			}
		}
		catch(Exception e)
		{
			throw new LoadEnvException("service.yaml", e);
		}
		finally
		{
			try
			{
				if(serviceFile != null) serviceFile.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
