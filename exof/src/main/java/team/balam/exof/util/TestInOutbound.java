package team.balam.exof.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;
import team.balam.exof.module.service.component.Outbound;
import team.balam.exof.module.service.component.OutboundExecuteException;

public class TestInOutbound implements Inbound, Outbound<String, Void>
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 *	Inbound에서 호출되는 메소드.
	 */
	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException
	{
		logger.info("Test Inbound. Service Path : " + _se.getServicePath());
	}
	
	/**
	 *	Outbound에서 호출되는 메소드.
	 */
	@Override
	public Void execute(String _result) throws OutboundExecuteException
	{
		logger.info("Test Outbound. In : " + _result);
		
		return null;
	}
}
