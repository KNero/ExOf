package team.balam.exof.test;

import team.balam.exof.module.service.component.Outbound;
import team.balam.exof.module.service.component.OutboundExecuteException;

public class TestOutbound implements Outbound<String, Void>
{
	@Override
	public Void execute(String _result) throws OutboundExecuteException
	{
		System.err.println("out : " + _result);
		
		return null;
	}
}
