package team.balam.exof.container.console;

public enum ServiceList {
	SHOW_SERVICE_LIST("getServiceList"),
	SHOW_SCHEDULE_LIST("getScheduleList"),
	LOGIN_WEB_CONSOLE("loginWebConsole");
	
	private String value;
	
	private ServiceList(String _value) {
		this.value = _value;
	}
	
	public String value() {
		return this.value;
	}
}
