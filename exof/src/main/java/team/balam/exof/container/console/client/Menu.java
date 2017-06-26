package team.balam.exof.container.console.client;

public interface Menu {
	String QUIT = "3";

	interface LevelOne {
		String GET = "1";
		String SET = "2";
	}

	interface Execute {
		String GET_SERVICE_LIST = "1.1";
		String GET_SCHEDULE_LIST = "1.2";
	}
}
