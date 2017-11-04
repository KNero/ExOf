package team.balam.exof.container.console.client.executor;

import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ServiceList;
import team.balam.exof.container.console.client.Client;
import team.balam.exof.container.console.client.Menu;

import java.io.IOException;
import java.util.Map;

/**
 * Created by kwonsm on 2017. 7. 8..
 * Console Monitor 의 framework 의 설정 값을 변경해 주는 서비스를 호출한다.
 */
class InfoSetter {
	InfoSetter(CommandExecutor executor) {
		executor.putExecutor(Menu.Execute.SET_SERVICE_VARIABLE, this::setServiceVariable);
		executor.putExecutor(Menu.Execute.SET_SCHEDULER_USE_ON_OFF, this::setSchedulerOnOff);
		executor.putExecutor(Menu.Execute.SET_SCHEDULER_CRON, this::setSchedulerCron);
		executor.putExecutor(Menu.Execute.ADD_DYNAMIC_SETTING, this::addDynamicSetting);
		executor.putExecutor(Menu.Execute.UPDATE_DYNAMIC_SETTING, this::updateDynamicSetting);
		executor.putExecutor(Menu.Execute.REMOVE_DYNAMIC_SETTING, this::removeDynamicSetting);
	}

	void setServiceVariable(Map<String, String> parameter) {
		String servicePath = parameter.get(Command.Key.SERVICE_PATH);
		String variableName = parameter.get(Command.Key.VARIABLE_NAME);
		String variableValue = parameter.get(Command.Key.VARIABLE_VALUE);

		Command command = new Command(ServiceList.SET_SERVICE_VARIABLE_VALUE);
		command.addParameter(Command.Key.SERVICE_PATH, servicePath);
		command.addParameter(Command.Key.VARIABLE_NAME, variableName);
		command.addParameter(Command.Key.VARIABLE_VALUE, variableValue);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void setSchedulerOnOff(Map<String, String> parameter) {
		String id = parameter.get(Command.Key.ID);
		String onOff = parameter.get(Command.Key.VALUE);

		Command command = new Command(ServiceList.SET_SCHEDULER_ON_OFF);
		command.addParameter(Command.Key.ID, id);
		command.addParameter(Command.Key.VALUE, onOff);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void setSchedulerCron(Map<String, String> parameter) {
		String id = parameter.get(Command.Key.ID);
		String cron = parameter.get(Command.Key.CRON);

		Command command = new Command(ServiceList.SET_SCHEDULER_CRON);
		command.addParameter(Command.Key.ID, id);
		command.addParameter(Command.Key.CRON, cron);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void addDynamicSetting(Map<String, String> parameter) {
		String name = parameter.get(Command.Key.NAME);
		String value = parameter.get(Command.Key.VALUE);
		String des = parameter.get(Command.Key.DESCRIPTION);

		Command command = new Command(ServiceList.ADD_DYNAMIC_SETTING);
		command.addParameter(Command.Key.NAME, name);
		command.addParameter(Command.Key.VALUE, value);
		command.addParameter(Command.Key.DESCRIPTION, des);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void updateDynamicSetting(Map<String, String> parameter) {
		String name = parameter.get(Command.Key.NAME);
		String value = parameter.get(Command.Key.VALUE);
		String des = parameter.get(Command.Key.DESCRIPTION);

		Command command = new Command(ServiceList.UPDATE_DYNAMIC_SETTING);
		command.addParameter(Command.Key.NAME, name);
		command.addParameter(Command.Key.VALUE, value);
		command.addParameter(Command.Key.DESCRIPTION, des);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void removeDynamicSetting(Map<String, String> parameter) {
		String name = parameter.get(Command.Key.NAME);
		Command command = new Command(ServiceList.REMOVE_DYNAMIC_SETTING);
		command.addParameter(Command.Key.NAME, name);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
