package team.balam.exof.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;

@ServiceDirectory
public class TestService
{
	private Logger logger  = LoggerFactory.getLogger(this.getClass());
	
	@Service
	public void receive(Object _req, String _a, String _b, String _c)
	{
		this.logger.info("Receive data : " + _req.toString());
	}
}
