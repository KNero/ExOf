package team.balam.exof.container.console.client;

import io.netty.util.internal.StringUtil;
import team.balam.exof.container.console.Command;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by kwonsm on 2017. 7. 6..
 * Viewer 에서 어떤 명령을 실행할 지 정해진 뒤 해당 명령에 필요한
 * 추가적인 parameter 를 입력받는다
 */
class CommandParameterReader {
	private BufferedReader standardReader;
	private ViewerCommand viewerCommand;

	CommandParameterReader(BufferedReader _standardReader, ViewerCommand _viewerCommand) {
		this.standardReader = _standardReader;
		this.viewerCommand = _viewerCommand;
	}

	public void execute() throws IOException {
		try {
			switch(this.viewerCommand.toString()) {
				case Menu.Execute.GET_SERVICE_LIST:
					this._readServicePath();
					break;

				case Menu.Execute.GET_SCHEDULE_LIST:
					this._readSchedulerId();
					break;

				case Menu.Execute.SET_SCHEDULER_USE_ON_OFF:
					this._readSchedulerOnOff();
					break;

				case Menu.Execute.GET_DYNAMIC_SETTING_SINGLE:
					this._readDynamicSettingSingleParameter();
					break;

				case Menu.Execute.SET_SERVICE_VARIABLE:
					this._readServiceVariableParameter();
					break;
			}
		} catch (TerminateException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	private void _readServicePath() throws IOException {
		System.out.print("Enter service path : ");
		String serviceName = this.standardReader.readLine().trim();

		if (!StringUtil.isNullOrEmpty(serviceName)) {
			this.viewerCommand.putParameter(Command.Key.SERVICE_PATH, serviceName);
		}
	}

	private void _readSchedulerId() throws IOException {
		System.out.print("Enter scheduler info : ");
		String id = this.standardReader.readLine().trim();

		if (!StringUtil.isNullOrEmpty(id)) {
			this.viewerCommand.putParameter(Command.Key.NAME, id);
		}
	}

	private void _readSchedulerOnOff() throws IOException {
		System.out.print("Enter scheduler ID : ");
		String id = this.standardReader.readLine().trim();

		if (!StringUtil.isNullOrEmpty(id)) {
			this.viewerCommand.putParameter(Command.Key.ID, id);
		} else {
			throw new TerminateException("scheduler id is empty.");
		}

		System.out.println();
		System.out.println("(1)on   (2)off   (9)quit");
		String cmd = this.standardReader.readLine().trim();
		int number;

		try {
			number = Integer.parseInt(cmd);
		} catch (NumberFormatException e) {
			throw new TerminateException();
		}

		if (number == 1 || number == 2) {
			this.viewerCommand.putParameter(Command.Key.VALUE, number == 1 ? "true" : "false");
		} else if (number == 9) {
			throw new TerminateException();
		} else {
			throw new TerminateException("There is no menu. (Enter number 1, 2 or 9)");
		}
	}

	private void _readDynamicSettingSingleParameter() throws IOException {
		System.out.print("Enter dynamic setting name : ");
		String name = this.standardReader.readLine().trim();

		if (!StringUtil.isNullOrEmpty(name)) {
			this.viewerCommand.putParameter(Command.Key.NAME, name);
		} else {
			throw new TerminateException("dynamic setting name is empty.");
		}
	}

	private void _readServiceVariableParameter() throws IOException {
		System.out.print("Enter service path : ");
		String serviceName = this.standardReader.readLine().trim();

		if (!StringUtil.isNullOrEmpty(serviceName)) {
			 this.viewerCommand.putParameter(Command.Key.SERVICE_PATH, serviceName);
		} else {
			throw new TerminateException("service name is empty.");
		}

		System.out.print("Enter variable name : ");
		String variableName = this.standardReader.readLine().trim();

		if (!StringUtil.isNullOrEmpty(variableName)) {
			this.viewerCommand.putParameter(Command.Key.VARIABLE_NAME, variableName);
		} else {
			throw new TerminateException("service variable name is empty.");
		}

		System.out.print("Enter variable value : ");
		String variableValue = this.standardReader.readLine().trim();

		if (!StringUtil.isNullOrEmpty(variableValue)) {
			this.viewerCommand.putParameter(Command.Key.VARIABLE_VALUE, variableValue);
		} else {
			throw new TerminateException("service variable value is empty.");
		}
	}
}
