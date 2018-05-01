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
		ServiceProvider.lookup("/test/external");
		ServiceProvider.lookup("/test/external/");
	}

	@Test
	public void callNormalService() throws ServiceNotFoundException {
		ServiceProvider.lookup("/one-service/testSingleMethod");
	}

	@Test
	public void callServiceDirectory() throws Exception {
		ServiceWrapper service = ServiceProvider.lookup("/autoScan/call-internal-service");
		service.call();
	}

	@Test(expected = TestException.class)
	public void throwException() throws Exception {
		ServiceWrapper service = ServiceProvider.lookup("/test/throwException");
		service.call();
	}
}
