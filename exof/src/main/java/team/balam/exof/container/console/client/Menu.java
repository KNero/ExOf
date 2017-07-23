package team.balam.exof.container.console.client;

public interface Menu {
	String QUIT = "9";

	interface LevelOne {
		String GET = "1";
		String SET = "2";
	}

	interface Execute {
		String GET_SERVICE_LIST = "1.1";
		String GET_SCHEDULE_LIST = "1.2";
		String GET_DYNAMIC_SETTING_LIST = "1.3";

		String SET_SERVICE_VARIABLE = "2.1";
		String SET_SCHEDULER_USE_ON_OFF = "2.2";
		String ADD_DYNAMIC_SETTING = "2.3";
		String UPDATE_DYNAMIC_SETTING = "2.4";
		String REMOVE_DYNAMIC_SETTING = "2.5";
	}
}
