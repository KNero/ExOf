package balam.exof.test.module.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.service.*;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Variable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kwonsm on 2017. 6. 25..
 * ServiceProvider test class
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ServiceDirectory
public class ServiceProviderTest {
	@Variable(serviceName = "testService")
	private String variable1;

	@Variable(serviceName = "testService")
	private List<String> variable2;

	private  static boolean isReloadTest;

	@team.balam.exof.module.service.annotation.Service
	public void testService(String _variable1, List<String> _variable2) {
		if (isReloadTest) {
			Assert.assertEquals("test222", _variable1);
			Assert.assertEquals("A1", _variable2.get(0));
			Assert.assertEquals("B1", _variable2.get(1));
			Assert.assertEquals("test222", this.variable1);
			Assert.assertEquals("A1", this.variable2.get(0));
			Assert.assertEquals("B1", this.variable2.get(1));
		} else {
			Assert.assertEquals("test111", _variable1);
			Assert.assertEquals("A", _variable2.get(0));
			Assert.assertEquals("B", _variable2.get(1));
			Assert.assertEquals("test111", this.variable1);
			Assert.assertEquals("A", this.variable2.get(0));
			Assert.assertEquals("B", this.variable2.get(1));
		}
	}

	@BeforeClass
	public static void loadService() throws Exception {
		ServiceDirectoryInfo info = new ServiceDirectoryInfo();
		info.setClassName("team.balam.exof.test.module.service.ServiceProviderTest");
		info.setPath("/test/serviceProviderTest");

		ServiceVariable serviceVariable = new ServiceVariable();
		serviceVariable.put("variable1", "test111");
		serviceVariable.put("variable2", "A");
		serviceVariable.put("variable2", "B");
		info.setVariable("testService", serviceVariable);

		List<ServiceDirectoryInfo> infoList = new LinkedList<>();
		infoList.add(info);
		SystemSetting.getInstance().set(EnvKey.FileName.SERVICE, EnvKey.Service.SERVICES, infoList);

		SystemSetting.getInstance().set(EnvKey.FileName.FRAMEWORK, EnvKey.Framework.AUTORELOAD_SERVICE_VARIABLE, true);
		ServiceProvider.getInstance().start();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_serviceVariableReload() throws Exception {
		ServiceDirectoryInfo info = new ServiceDirectoryInfo();
		info.setClassName("team.balam.exof.test.module.service.ServiceProviderTest");
		info.setPath("/test/serviceProviderTest");

		ServiceVariable serviceVariable = new ServiceVariable();
		serviceVariable.put("variable1", "test222");
		serviceVariable.put("variable2", "A1");
		serviceVariable.put("variable2", "B1");
		info.setVariable("testService", serviceVariable);

		List<ServiceDirectoryInfo> infoList = new LinkedList<>();
		infoList.add(info);
		SystemSetting.getInstance().set(EnvKey.FileName.SERVICE, EnvKey.Service.SERVICES, infoList);

		ServiceProvider.getInstance().update(null, null);

		isReloadTest = true;

		ServiceObject serviceObject = new ServiceObject("/test/serviceProviderTest/testService");
		Service service = ServiceProvider.lookup(serviceObject.getServicePath());
		service.call(serviceObject);
	}
}