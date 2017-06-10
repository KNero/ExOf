package team.balam.exof.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;

import java.util.List;

@ServiceDirectory
public class TestService {
	private Logger logger  = LoggerFactory.getLogger(this.getClass());
	
	@Service
	@Inbound(classObject=TestInbound.class)
	@Outbound(classObject=TestOutbound.class)
	public String schedule(String _a, String _b, String _c) {
		this.logger.info("Service Variable : " + _a + " / " + _b + " / " + _c);
		
		return "END";
	}

	@Service
	public void arrayParam(String _a, String _b, List<String> _c) {
		this.logger.info("Service Variable : " + _a + " / " + _b + " / " + _c);
	}
	
	@Service
	@Inbound(classObject=TestInbound.class)
	@Outbound(classObject=TestOutbound.class)
	public void receive(Object _req) {
		this.logger.info("Receive data : " + _req.toString());
		
		RequestContext.writeResponse("response\0".getBytes());
	}
}
