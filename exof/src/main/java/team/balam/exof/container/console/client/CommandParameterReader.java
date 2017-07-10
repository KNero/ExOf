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
			 this.viewerCommand.putParameter("serviceName", serviceName);
		} else {
			throw new TerminateException("service name is empty.");
		}

		System.out.print("Enter variable name : ");
		String variableName = this.standardReader.readLine().trim();

		if (!StringUtil.isNullOrEmpty(variableName)) {
			this.viewerCommand.putParameter("variableName", variableName);
		} else {
			throw new TerminateException("service variable name is empty.");
		}

		System.out.print("Enter variable value : ");
		String variableValue = this.standardReader.readLine().trim();

		if (!StringUtil.isNullOrEmpty(variableValue)) {
			this.viewerCommand.putParameter("variableName", variableValue);
		} else {
			throw new TerminateException("service variable value is empty.");
		}
	}
}
