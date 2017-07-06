package team.balam.exof.container.console.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * ViewerCommand 에 해당되는 서비스 호출 실행하는 Delegate 를 호출한다. <br>
 * ServiceDelegate : InfoGetter, InfoSetter
 */
public class Executor {
	private static Map<String, Consumer<Map<String, Object>>> executorList = new HashMap<>();
	
	private static InfoGetter getter = new InfoGetter();

	static {
		executorList.put(Menu.Execute.GET_SERVICE_LIST, parameter -> getter.getServiceList());
		executorList.put(Menu.Execute.GET_SCHEDULE_LIST, parameter -> getter.getScheduleList());
		executorList.put(Menu.Execute.GET_DYNAMIC_SETTING_LIST, parameter -> getter.getDynamicSettingList());
		executorList.put(Menu.Execute.GET_DYNAMIC_SETTING_SINGLE, parameter -> getter.getDynamicSettingSingle((String) parameter.get("name")));
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
