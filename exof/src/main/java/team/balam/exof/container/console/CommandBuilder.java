package team.balam.exof.container.console;

public interface CommandBuilder
{
	static Command buildServiceListGetter()
	{
		return new Command(Command.Type.SHOW_SERVICE_LIST);
	}
}
