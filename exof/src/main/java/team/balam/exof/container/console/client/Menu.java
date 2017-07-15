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
		String GET_DYNAMIC_SETTING_SINGLE = "1.4";

		String SET_SERVICE_VARIABLE = "2.1";
		String SET_SCHEDULER_USE_ON_OFF = "2.2";
	}
}
