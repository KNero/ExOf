package team.balam.exof.container.console;

public enum ServiceList {
	GET_SERVICE_LIST("getServiceList"),
	GET_SCHEDULE_LIST("getScheduleList"),
	LOGIN_ADMIN_CONSOLE("loginAdminConsole"),
	GET_DYNAMIC_SETTING_LIST("getDynamicSettingList"),
	SET_SERVICE_VARIABLE_VALUE("setServiceVariableValue"),
	SET_SCHEDULER_ON_OFF("setSchedulerOnOff");
	
	private String value;
	
	ServiceList(String _value) {
		this.value = _value;
	}
	
	public String value() {
		return this.value;
	}
}
