package balam.exof.test.module.service;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.Variable;

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

	@Service
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
}