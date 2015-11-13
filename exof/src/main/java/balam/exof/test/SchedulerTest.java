package balam.exof.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.service.annotation.Inbound;
import balam.exof.service.annotation.Outbound;
import balam.exof.service.annotation.Service;
import balam.exof.service.annotation.ServiceDirectory;

@ServiceDirectory
public class SchedulerTest
{
	@Service(name="testScheduler")
	@Inbound(className="balam.exof.test.TestInbound")
	@Outbound(className="balam.exof.test.TestOutbound")
	public String execute(String _a, String _b, String _c) throws Exception
	{
		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.info("Scheduler execute : {} / {} / {}", _a, _b, _c);
		
//		Thread.sleep(100000);
		return "Success";
	}
}
