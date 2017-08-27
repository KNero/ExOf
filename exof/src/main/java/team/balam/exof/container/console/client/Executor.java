package team.balam.exof.container.console.client;

import team.balam.exof.container.console.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * ViewerCommand 에 해당되는 서비스 호출 실행하는 Delegate 를 호출한다. <br>
 * ServiceDelegate : InfoGetter, InfoSetter
 */
public class Executor {
	private static Map<String, Consumer<Map<String, String>>> executorList = new HashMap<>();
	
	private static InfoGetter getter = new InfoGetter();
	private static InfoSetter setter = new InfoSetter();

	static {
		executorList.put(Menu.Execute.GET_SERVICE_LIST, parameter -> getter.getServiceList(parameter.get(Command.Key.SERVICE_PATH)));
		executorList.put(Menu.Execute.GET_SCHEDULE_LIST, parameter -> getter.getScheduleList(parameter.get(Command.Key.NAME)));
		executorList.put(Menu.Execute.GET_DYNAMIC_SETTING_LIST, parameter -> getter.getDynamicSettingList(parameter.get(Command.Key.NAME)));
		executorList.put(Menu.Execute.SET_SERVICE_VARIABLE, parameter -> {
			String servicePath = parameter.get(Command.Key.SERVICE_PATH);
			String variableName = parameter.get(Command.Key.VARIABLE_NAME);
			String variableValue = parameter.get(Command.Key.VARIABLE_VALUE);

			setter.setServiceVariable(servicePath, variableName, variableValue);
		});
		executorList.put(Menu.Execute.GET_PORT_INFO, parameter -> getter.getPortInfo());

		executorList.put(Menu.Execute.SET_SCHEDULER_USE_ON_OFF, parameter -> {
			String id = parameter.get(Command.Key.ID);
			String onOff = parameter.get(Command.Key.VALUE);

			setter.setSchedulerOnOff(id, onOff);
		});
		executorList.put(Menu.Execute.SET_SCHEDULER_CRON, parameter -> {
			String id = parameter.get(Command.Key.ID);
			String cron = parameter.get(Command.Key.CRON);

			setter.setSchedulerCron(id, cron);
		});
		executorList.put(Menu.Execute.ADD_DYNAMIC_SETTING, parameter -> {
			String name = parameter.get(Command.Key.NAME);
			String value = parameter.get(Command.Key.VALUE);
			String des = parameter.get(Command.Key.DESCRIPTION);

			setter.addDynamicSetting(name, value, des);
		});
		executorList.put(Menu.Execute.UPDATE_DYNAMIC_SETTING, parameter -> {
			String name = parameter.get(Command.Key.NAME);
			String value = parameter.get(Command.Key.VALUE);
			String des = parameter.get(Command.Key.DESCRIPTION);

			setter.updateDynamicSetting(name, value, des);
		});
		executorList.put(Menu.Execute.REMOVE_DYNAMIC_SETTING, parameter -> {
			String name = parameter.get(Command.Key.NAME);

			setter.removeDynamicSetting(name);
		});
	}

	public static void execute(ViewerCommand command) throws NotFoundOperation {
		String key = command.toString();

		if (executorList.containsKey(key)) {
			executorList.get(key).accept(command.getParameter());
		} else {
			throw new NotFoundOperation();
		}
	}
}
