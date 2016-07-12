package team.balam.exof.service.component;

public class OutboundExecuteException extends Exception
{
	private static final long serialVersionUID = -8498366693242172187L;
	
	public OutboundExecuteException(Exception _e)
	{
		super(_e);
	}
	
	public OutboundExecuteException(String _msg, Exception _e)
	{
		super(_msg, _e);
	}
}
