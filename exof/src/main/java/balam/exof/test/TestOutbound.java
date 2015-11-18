package balam.exof.test;

import balam.exof.service.component.Outbound;
import balam.exof.service.component.OutboundExecuteException;

public class TestOutbound implements Outbound<String, Void>
{
	@Override
	public Void execute(String _result) throws OutboundExecuteException
	{
		System.err.println("out : " + _result);
		
		return null;
	}
}
