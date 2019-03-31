package team.balam.exof.test.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.annotation.*;
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

	public String variable1;
	public String variable2;
	public String variable3;
	public String variable4;
    public int variable5;

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

	@RestService(method = HttpMethod.GET, name = "/rest")
	public void get() {

	}

	@RestService(method = HttpMethod.POST, name = "rest")
	public void post() {

	}

	@RestService(method = HttpMethod.GET, name = "/rest/{variable1}/{variable2}")
	public void get1(@Variable("variable1") String variable1, @Variable("variable2") String variable2) {
		this.variable1 = variable1;
		this.variable2 = variable2;
	}

	@RestService(method = HttpMethod.GET, name = "/rest/get2")
    public void get2(@Variable("a") String a, @Variable("b") String b) {
	    variable1 = a;
	    variable2 = b;
    }

    @RestService(method = HttpMethod.POST, name = "/rest/post1/{variable1}", bodyToObject = TestObject.class)
    public void post1(@Variable("variable1") String variable1, @Variable("a") String a, TestObject object) {
	    this.variable1 = variable1;
	    variable2 = a;
        variable3 = object.a;
        variable4 = object.b;
        variable5 = object.c;
    }

    public static class BaseObject {
        protected String a;
        protected String b;
    }

    public static class TestObject extends BaseObject {
        private int c;
    }
}
