package team.balam.exof.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.service.component.Outbound;
import team.balam.exof.module.service.component.OutboundExecuteException;

public class TestOutbound implements Outbound<String, Void>
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Void execute(String _result) throws OutboundExecuteException
	{
		logger.info("Test Outbound. In : " + _result);
		
		return null;
	}
}
