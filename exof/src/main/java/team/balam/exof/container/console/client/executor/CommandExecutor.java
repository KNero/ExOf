package team.balam.exof.container.console.client.executor;

import team.balam.exof.container.console.client.NotFoundOperation;
import team.balam.exof.container.console.client.ViewerCommand;
import team.balam.exof.container.console.client.executor.EtcExecutor;
import team.balam.exof.container.console.client.executor.InfoGetter;
import team.balam.exof.container.console.client.executor.InfoSetter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * ViewerCommand 에 해당되는 서비스 호출 실행하는 Delegate 를 호출한다. <br>
 * ServiceDelegate : InfoGetter, InfoSetter
 */
public class CommandExecutor {
	private Map<String, Consumer<Map<String, String>>> executorList = new HashMap<>();

	public CommandExecutor() {
		new InfoSetter(this);
		new InfoGetter(this);
		new EtcExecutor(this);
	}

	void putExecutor(String key, Consumer<Map<String, String>> consumer) {
		this.executorList.put(key, consumer);
	}

	public void execute(ViewerCommand command) throws NotFoundOperation {
		String key = command.toString();

		if (this.executorList.containsKey(key)) {
			this.executorList.get(key).accept(command.getParameter());
		} else {
			throw new NotFoundOperation();
		}
	}
}
