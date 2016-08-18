package team.balam.exof.container.console;

public interface CommandBuilder
{
	static Command buildServiceListGetter()
	{
		return new Command(Command.Type.SHOW_SERVICE_LIST);
	}
	
	static Command buildScheduleListGetter()
	{
		return new Command(Command.Type.SHOW_SCHEDULE_LIST);
	}
}
