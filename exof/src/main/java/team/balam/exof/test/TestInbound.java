package team.balam.exof.test;

import team.balam.exof.service.ServiceObject;
import team.balam.exof.service.component.Inbound;
import team.balam.exof.service.component.InboundExecuteException;

public class TestInbound implements Inbound
{
	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException
	{
		System.err.println("in : " + _se);
	}
}
