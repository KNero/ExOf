package team.balam.exof.container.console.client;

import java.util.HashMap;
import java.util.Map;

public class Executor {
	private static Map<String, Runnable> executorList = new HashMap<String, Runnable>();
	
	private static InfoGetter getter = new InfoGetter();

	static {
		executorList.put(Menu.Execute.GET_SERVICE_LIST, () -> {
			getter.getServiceList();
		});

		executorList.put(Menu.Execute.GET_SCHEDULE_LIST, () -> {
			getter.getScheduleList();
		});

		executorList.put(Menu.Execute.SHOW_DYNAMIC_SETTING_LIST, () -> {
			getter.getDynamicSettingList();
		});
	}

	public static void execute(ViewerCommand command) throws NotFoundOperation {
		String key = command.toString();

		if (executorList.containsKey(key)) {
			executorList.get(key).run();
		} else {
			throw new NotFoundOperation();
		}
	}
}
