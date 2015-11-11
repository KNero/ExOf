package balam.exof.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.service.annotation.Service;
import balam.exof.service.annotation.ServiceDirectory;

@ServiceDirectory
public class SchedulerTest
{
	@Service
	public void execute(String _a, String _b, String _c)
	{
		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.info("Scheduler execute : {} / {} / {}", _a, _b, _c);
	}
}
