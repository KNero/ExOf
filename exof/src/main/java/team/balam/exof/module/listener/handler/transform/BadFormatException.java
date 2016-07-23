package team.balam.exof.module.listener.handler.transform;

public class BadFormatException extends Exception
{
	private static final long serialVersionUID = 7167793189718703667L;
	
	public BadFormatException(String _msg)
	{ 
		super(_msg);
	}
	
	public BadFormatException(String _msg, Exception _exp)
	{ 
		super(_msg, _exp);
	}
	
	public BadFormatException(Exception _exp)
	{ 
		super(_exp);
	}
}
