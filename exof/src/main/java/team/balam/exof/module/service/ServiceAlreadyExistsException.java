package team.balam.exof.module.service;

public class ServiceAlreadyExistsException extends Exception
{
	private static final long serialVersionUID = -5949556060519675079L;
	
	public ServiceAlreadyExistsException(String _servicePath)
	{
		super("Service has already been registered. [" + _servicePath + "]");
	}
}
