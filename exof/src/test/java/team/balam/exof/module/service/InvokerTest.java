package team.balam.exof.module.service;

import io.netty.handler.codec.http.HttpRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
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

	@Test
	public void callOldHttpGet() throws Exception {
		HttpRequest request = PowerMockito.mock(HttpRequest.class);
		PowerMockito.when(request.method()).thenReturn(io.netty.handler.codec.http.HttpMethod.GET);
		PowerMockito.when(request.uri()).thenReturn("?list[]=권1&list[]=권2&list[]=권3&list[]=권4&paramA=pA&paramB=pB&name=권성민");

		ServiceObject so = new ServiceObject("/test/http-get2");
		so.setRequest(request);

		ServiceWrapper service = ServiceProvider.lookup(so);
		service.call(so);
	}
}
