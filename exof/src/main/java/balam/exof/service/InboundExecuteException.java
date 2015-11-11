package balam.exof.service;

public class InboundExecuteException extends Exception 
{
	private static final long serialVersionUID = -8935499671612950840L;
	
	public InboundExecuteException(Exception _e)
	{
		super(_e);
	}
}
