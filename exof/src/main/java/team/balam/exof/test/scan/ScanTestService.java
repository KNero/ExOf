package team.balam.exof.test.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.test.TestInbound;
import team.balam.exof.test.TestOutbound;

@ServiceDirectory(path = "/autoScan")
public class ScanTestService {
	private Logger logger  = LoggerFactory.getLogger(this.getClass());

	private String a = "A";
	private String b = "B";
	private String c = "C";

	@Service(name = "autoSchedule"/*, schedule = "0/5 * * * * ?"*/)
	@Inbound(TestInbound.class)
	@Outbound(TestOutbound.class)
	public String schedule() {
		this.logger.info("Auto Scan Service Variable : " + this.a + " / " + this.b + " / " + this.c);

		return "END";
	}
}
