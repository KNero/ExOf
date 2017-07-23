package team.balam.exof.container.console.client;

import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ServiceList;
import team.balam.exof.environment.EnvKey;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class InfoGetter
{
	@SuppressWarnings("unchecked")
	void getServiceList(String _servicePath)
	{
		try
		{
			Command command = new Command(ServiceList.GET_SERVICE_LIST);
			command.addParameter(Command.Key.SERVICE_PATH, _servicePath);

			Client.send(command, _result -> {
				Map<String, Object> resultMap = (Map<String, Object>) _result;
				resultMap.forEach((_key, _value) -> {
					if (_value == null) {
						return;
					}

					StringBuilder infoLog = new StringBuilder();
					AtomicInteger serviceSize = new AtomicInteger();

					Map<String, Object> valueMap = (Map<String, Object>)_value;
					infoLog.append("Directory path : ").append(_key).append("\n");
					infoLog.append("Class : ").append(valueMap.get(Command.Key.CLASS)).append("\n");
					infoLog.append("Service list").append("\n");

					valueMap.keySet().forEach(_valueKey -> {
						if(! Command.Key.CLASS.equals(_valueKey)) {
							if(! _valueKey.endsWith(EnvKey.Service.SERVICE_VARIABLE) && valueMap.get(_valueKey) != null) {
								serviceSize.incrementAndGet();

								infoLog.append(" -s- ").append(_valueKey).append("(method name : ").append(valueMap.get(_valueKey)).append(")").append("\n");
								
								Map<String, Object> variables = (Map<String, Object>) valueMap.get(_valueKey + EnvKey.Service.SERVICE_VARIABLE);
								variables.keySet().forEach(_name -> infoLog.append("   -v- ").append(_name).append(" : ").append(variables.get(_name).toString()).append("\n"));
							}
						}
					});

					if (serviceSize.get() > 0) {
						System.out.println(infoLog);
					}
				});
			}, null);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	void getScheduleList(String _schedulerId)
	{
		try
		{
			Command command = new Command(ServiceList.GET_SCHEDULE_LIST);
			command.addParameter(Command.Key.NAME, _schedulerId);

			Client.send(command, _result -> {
				Map<String, Object> resultMap = (Map<String, Object>) _result;
				List<String> list = (List<String>) resultMap.get("list");

				list.forEach(scheduleName -> System.out.println("- " + scheduleName));
			}, null);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	void getDynamicSettingList(String _name) {
		Command command = new Command(ServiceList.GET_DYNAMIC_SETTING_LIST);
		command.addParameter(Command.Key.NAME, _name);

		try {
			Client.send(command, _result -> {
				List<Map<String, Object>> resultList = (List<Map<String, Object>>) _result;
				if (resultList.size() > 0) {
					for(Map<String, Object> m : resultList) {
						StringBuilder info = new StringBuilder();
						info.append("name [").append(m.get("name"));
						info.append("] / value [").append(m.get("value"));
						info.append("] / description [").append(m.get("description")).append("]");
						System.out.println(info);
					}
				} else {
					System.out.println("No data.");
				}
			}, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
