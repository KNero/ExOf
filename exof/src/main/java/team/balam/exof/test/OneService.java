package team.balam.exof.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Shutdown;
import team.balam.exof.module.service.annotation.Startup;
import team.balam.exof.module.service.annotation.Variable;

import java.util.List;

@ServiceDirectory
public class OneService {
	private Logger log = LoggerFactory.getLogger(OneService.class);

	@Variable
	public String a;

	@Variable
	public String b;

	@Variable
	public List<String> c;

	@Service("/autoScan2/autoSchedule")
	public ServiceWrapper service;

	@Startup
	public void init() {
		log.info("Startup one-service.");
	}

	@Service("testSingleMethod")
	public void testSingleMethod() {
		log.info("a: {}, b: {}, c: {}", this.a, this.b, this.c);
	}

	@Shutdown
	public void shutdown() {
		log.info("Shutdown one-service.");
	}
}
