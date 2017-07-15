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
			Client.send(command, _result -> System.out.println("Result value : " + _result.toString()),
					null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void setSchedulerOnOff(String _id, String _onOff) {
		Command command = new Command(ServiceList.SET_SCHEDULER_ON_OFF);
		command.addParameter(Command.Key.ID, _id);
		command.addParameter(Command.Key.VALUE, _onOff);

		try {
			Client.send(command, _result -> System.out.println(_result.toString()),
					null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
