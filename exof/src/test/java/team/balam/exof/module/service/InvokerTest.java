package team.balam.exof.module.service;

import org.junit.BeforeClass;
import org.junit.Test;
import team.balam.exof.TestInitializer;
import team.balam.exof.environment.ServiceLoader;
import team.balam.exof.test.TestException;

public class InvokerTest {
	@BeforeClass
	public static void init() throws Exception {
		TestInitializer.init();
		new ServiceLoader().load("./env");
		ServiceProvider.getInstance().start();
	}

	@Test
	public void callEmptyNameService() throws ServiceNotFoundException {
		ServiceProvider.lookup(new ServiceObject("/test/external"));
		ServiceProvider.lookup(new ServiceObject("/test/external/"));
	}

	@Test
	public void callNormalService() throws ServiceNotFoundException {
		ServiceProvider.lookup(new ServiceObject("/one-service/testSingleMethod"));
	}

	@Test
	public void callServiceDirectory() throws Exception {
		ServiceWrapper service = ServiceProvider.lookup(new ServiceObject("/autoScan/call-internal-service"));
		service.call();
	}

	@Test(expected = TestException.class)
	public void throwException() throws Exception {
		ServiceWrapper service = ServiceProvider.lookup(new ServiceObject("/test/throwException"));
		service.call();
	}
}
