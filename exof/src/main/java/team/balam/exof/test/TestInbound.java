package team.balam.exof.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;

public class TestInbound implements Inbound
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException
	{
		logger.info("Test Inbound. Service Path : " + _se.getServicePath());
	}
}
