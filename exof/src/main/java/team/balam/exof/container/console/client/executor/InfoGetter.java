package team.balam.exof.container.console.client.executor;

import team.balam.exof.Constant;
import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ServiceList;
import team.balam.exof.container.console.client.Client;
import team.balam.exof.container.console.client.Menu;
import team.balam.exof.environment.EnvKey;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class InfoGetter
{
	InfoGetter(CommandExecutor executor) {
		executor.putExecutor(Menu.Execute.GET_SERVICE_LIST, this::getServiceList);
		executor.putExecutor(Menu.Execute.GET_SCHEDULE_LIST, this::getScheduleList);
		executor.putExecutor(Menu.Execute.GET_DYNAMIC_SETTING_LIST, this::getDynamicSettingList);
		executor.putExecutor(Menu.Execute.GET_PORT_INFO, this::getPortInfo);
	}

	@SuppressWarnings("unchecked")
	void getServiceList(Map<String, String> parameter)
	{
		String servicePath = parameter.get(Command.Key.SERVICE_PATH);
		try
		{
			Command command = new Command(ServiceList.GET_SERVICE_LIST);
			command.addParameter(Command.Key.SERVICE_PATH, servicePath);

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

								String callPath = _key + (!_valueKey.isEmpty() ? Constant.SERVICE_SEPARATE + _valueKey : "");
								infoLog.append(" -s- ").append(callPath).append(" (method name : ").append(valueMap.get(_valueKey)).append(")").append("\n");
								
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
	void getScheduleList(Map<String, String> parameter)
	{
		String schedulerId = parameter.get(Command.Key.NAME);
		try
		{
			Command command = new Command(ServiceList.GET_SCHEDULE_LIST);
			command.addParameter(Command.Key.NAME, schedulerId);

			Client.send(command, _result -> {
				List<String> list = (List<String>) _result;
				list.forEach(scheduleName -> System.out.println("- " + scheduleName));
			}, null);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	void getDynamicSettingList(Map<String, String> parameter) {
		String name = parameter.get(Command.Key.NAME);
		Command command = new Command(ServiceList.GET_DYNAMIC_SETTING_LIST);
		command.addParameter(Command.Key.NAME, name);

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

	@SuppressWarnings("unchecked")
	void getPortInfo(Map<String, String> parameter) {
		Command command = new Command(ServiceList.GET_PORT_INFO);

		try {
			Client.send(command, _result -> {
				List<Map<String, Object>> resultList = (List<Map<String, Object>>) _result;
				if (resultList.size() > 0) {
					resultList.forEach(info -> {
						int port = (Integer) info.get(Command.Key.PORT);
						List<Map<String, Object>> attribute = (List<Map<String, Object>>) info.get(Command.Key.ATTRIBUTE);
						List<Map<String, Object>> childAttribute = (List<Map<String, Object>>) info.get(Command.Key.CHILD_ATTRIBUTE);

						System.out.println("Port : " + port);
						System.out.print(" - Attribute : ");
						for (Map<String, Object> attr : attribute) {
							System.out.print("[" + attr.get("key") + "=" + attr.get("value") + "]");
						}
						System.out.println();

						for (Map<String, Object> attr : childAttribute) {
							System.out.println(" - " + attr.get("node_name") + " : " + attr.get("value"));
						}
						System.out.println();
					});
				} else {
					System.out.println("No data.");
				}
			}, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
