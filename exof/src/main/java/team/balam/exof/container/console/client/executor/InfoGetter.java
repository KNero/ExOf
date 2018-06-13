package team.balam.exof.container.console.client.executor;

import io.netty.util.internal.StringUtil;
import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ServiceList;
import team.balam.exof.container.console.client.Client;
import team.balam.exof.container.console.client.Menu;
import team.balam.exof.environment.EnvKey;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

			Client.send(command, result -> {
				Map<String, Object> resultMap = (Map<String, Object>) result;
				resultMap.forEach((key, value) -> {
					Map<String, Object> info = (Map<String, Object>) value;
					System.out.println("ServiceDirectory : " + key + " (" + info.get(EnvKey.Service.CLASS) + ")");

					Map<String, Object> variable = (Map<String, Object>) info.get(EnvKey.Service.SERVICE_VARIABLE);
					if (!variable.isEmpty()) {
						System.out.println("ServiceVariable ");
						variable.forEach((vk, vv) -> System.out.println("\t(V) " + vk + ": " + vv));
						System.out.println();
					}

					List<Object> services = (List<Object>) info.get("services");
					for (Object serviceInfo : services) {
						Map<String, String> m = (Map<String, String>) serviceInfo;

						if (StringUtil.isNullOrEmpty(servicePath)) {
							m.forEach((k, v) -> System.out.println("\t" + k + ": " + v));
							System.out.println();
						} else if (m.get("path").contains(servicePath)) {
							m.forEach((k, v) -> System.out.println("\t" + k + ": " + v));
							System.out.println();
						}
					}
				});
			}, null);
		}
		catch(IOException e) {
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
