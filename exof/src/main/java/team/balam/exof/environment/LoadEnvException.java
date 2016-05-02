package team.balam.exof.environment;

public class LoadEnvException extends Exception
{
	private static final long serialVersionUID = -5947048134986004420L;
	
	public LoadEnvException(String _name)
	{
		super("Can not load your settings. [" + _name + "]");
	}
	
	public LoadEnvException(String _name, Exception _e)
	{
		super("Can not load your settings. [" + _name + "]", _e);
	}
}
