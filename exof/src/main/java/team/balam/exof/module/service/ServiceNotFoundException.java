package team.balam.exof.service;

public class ServiceNotFoundException extends Exception
{
	private static final long serialVersionUID = 4465113623320047789L;
	
	public ServiceNotFoundException(String _name)
	{
		super("Can not find the service[" + _name + "].");
	}
}