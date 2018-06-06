package team.balam.exof.test.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.component.http.HttpMethod;
import team.balam.exof.module.service.component.http.RestService;
import team.balam.exof.test.TestInbound;
import team.balam.exof.test.TestOutbound;

@ServiceDirectory("/autoScan")
public class ScanTestService {
	private Logger logger  = LoggerFactory.getLogger(this.getClass());

	private String a = "A";
	private String b = "B";
	private String c = "C";

	@ServiceDirectory("/internal")
	private InternalService internalService;

	@Service(name = "autoSchedule"/*, schedule = "0/5 * * * * ?"*/)
	@Inbound(TestInbound.class)
	@Outbound(TestOutbound.class)
	public String schedule() {
		this.logger.info("Auto Scan Service Variable : {} / {} / {}", a, b, c);

		return "END";
	}

	@Service("call-internal-service")
	public void callInternalService() {
		if (!"test_test2".equals(this.internalService.test2())) {
			throw new RuntimeException("not equals return value.");
		}
	}

	@RestService(method = HttpMethod.GET, name = "rest")
	public void get() {

	}

	@RestService(method = HttpMethod.POST, name = "rest")
	public void post() {

	}
}
