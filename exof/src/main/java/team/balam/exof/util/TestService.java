package team.balam.exof.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;

@ServiceDirectory
public class TestService
{
	private Logger logger  = LoggerFactory.getLogger(this.getClass());
	
	@Inbound(classObject=TestInOutbound.class)
	@Outbound(classObject=TestInOutbound.class)
	@Service
	public String schedule(String _a, String _b, String _c)
	{
		this.logger.info("Service Variable : " + _a + " / " + _b + " / " + _c);
		
		return "END";
	}
	
	@Service
	public void receive(Object _req)
	{
		this.logger.info("Receive data : " + _req.toString());
		
		RequestContext.writeResponse("response\0".getBytes());
	}
}
