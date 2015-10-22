package balam.exof.scheduler;

public class ReloadException extends Exception 
{
	private static final long serialVersionUID = 166292007538067111L;

	public ReloadException(String _key, String _value)
	{
		super("Parameter reloading failed. [" + _key + "] = [" + _value + "]");
	}
	
	public ReloadException(String _msg)
	{
		super("Parameter reloading failed. (" + _msg + ")");
	}
}
