package team.balam.exof.container.console.client;

import io.netty.util.internal.StringUtil;

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

	public CommandParameterReader(BufferedReader _standardReader, ViewerCommand _viewerCommand) {
		this.standardReader = _standardReader;
		this.viewerCommand = _viewerCommand;
	}

	public void execute() throws IOException {
		switch(this.viewerCommand.toString()) {
			case Menu.Execute.GET_DYNAMIC_SETTING_SINGLE:
				this._readDynamicSettingSingleParameter();
				break;
		}
	}

	private void _readDynamicSettingSingleParameter() throws IOException {
		System.out.print("Enter dynamic setting name : ");
		String name = this.standardReader.readLine();

		if (!StringUtil.isNullOrEmpty(name)) {
			this.viewerCommand.putParameter("name", name);
		} else {
			throw new TerminateException();
		}
	}
}
