package balam.exof.test;

import balam.exof.service.component.Outbound;
import balam.exof.service.component.OutboundExecuteException;

public class TestOutbound implements Outbound
{
	@Override
	public void execute(Object _result) throws OutboundExecuteException
	{
		System.err.println("out : " + _result);
	}
}
