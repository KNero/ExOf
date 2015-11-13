package balam.exof.test;

import balam.exof.service.ServiceObject;
import balam.exof.service.component.Inbound;
import balam.exof.service.component.InboundExecuteException;

public class TestInbound implements Inbound
{
	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException
	{
		System.err.println("in : " + _se);
	}
}
