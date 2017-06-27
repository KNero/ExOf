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
		String SHOW_DYNAMIC_SETTING_LIST = "1.3";
	}
}
