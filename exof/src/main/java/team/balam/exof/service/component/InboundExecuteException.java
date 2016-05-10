package team.balam.exof.service.component;

public class InboundExecuteException extends Exception 
{
	private static final long serialVersionUID = -8935499671612950840L;
	
	public InboundExecuteException(Exception _e)
	{
		super(_e);
	}
	
	public InboundExecuteException(String _msg, Exception _e)
	{
		super(_msg, _e);
	}
}
