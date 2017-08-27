package team.balam.exof.container.console.client;

import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ServiceList;

import java.io.IOException;

/**
 * Created by kwonsm on 2017. 7. 8..
 * Console Monitor 의 framework 의 설정 값을 변경해 주는 서비스를 호출한다.
 */
class InfoSetter {
	void setServiceVariable(String _servicePath, String _variableName, String _variableValue) {
		Command command = new Command(ServiceList.SET_SERVICE_VARIABLE_VALUE);
		command.addParameter(Command.Key.SERVICE_PATH, _servicePath);
		command.addParameter(Command.Key.VARIABLE_NAME, _variableName);
		command.addParameter(Command.Key.VARIABLE_VALUE, _variableValue);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void setSchedulerOnOff(String _id, String _onOff) {
		Command command = new Command(ServiceList.SET_SCHEDULER_ON_OFF);
		command.addParameter(Command.Key.ID, _id);
		command.addParameter(Command.Key.VALUE, _onOff);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void setSchedulerCron(String _id, String _cron) {
		Command command = new Command(ServiceList.SET_SCHEDULER_CRON);
		command.addParameter(Command.Key.ID, _id);
		command.addParameter(Command.Key.CRON, _cron);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void addDynamicSetting(String _name, String _value, String _des) {
		Command command = new Command(ServiceList.ADD_DYNAMIC_SETTING);
		command.addParameter(Command.Key.NAME, _name);
		command.addParameter(Command.Key.VALUE, _value);
		command.addParameter(Command.Key.DESCRIPTION, _des);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void updateDynamicSetting(String _name, String _value, String _des) {
		Command command = new Command(ServiceList.UPDATE_DYNAMIC_SETTING);
		command.addParameter(Command.Key.NAME, _name);
		command.addParameter(Command.Key.VALUE, _value);
		command.addParameter(Command.Key.DESCRIPTION, _des);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void removeDynamicSetting(String _name) {
		Command command = new Command(ServiceList.REMOVE_DYNAMIC_SETTING);
		command.addParameter(Command.Key.NAME, _name);

		try {
			Client.send(command, System.out::println, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
