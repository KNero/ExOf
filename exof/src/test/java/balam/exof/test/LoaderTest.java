package balam.exof.test;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import team.balam.exof.Constant;
import team.balam.exof.container.SchedulerManager;
import team.balam.exof.environment.vo.SchedulerInfo;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.FrameworkLoader;
import team.balam.exof.environment.ServiceLoader;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.environment.vo.ServiceVariable;
import team.balam.exof.module.service.Service;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.util.sqlite.connection.DatabaseLoader;

public class LoaderTest
{
	@Before
	public void init() throws Exception {
		new File("./env/" + Constant.ENV_DB).delete();
		DatabaseLoader.load(Constant.ENV_DB, "./env/" + Constant.ENV_DB);
		ServiceInfoDao.initTable();
	}

	@Test
	public void testGetFrameworkExternal() throws Exception
	{
		FrameworkLoader loader = new FrameworkLoader();
		loader.load("./env");
		
		Map<String, Object> extMap = SystemSetting.getInstance().getExternal();
		Assert.assertEquals("abcde", extMap.get("test"));
	}

	@Test
	public void test_scheduleAndServiceDirectory() throws Exception {
		ServiceLoader loader = new ServiceLoader();
		loader.load("./env");

		List<SchedulerInfo> schedulerInfos = ServiceInfoDao.selectScheduler();
		Assert.assertEquals(2, schedulerInfos.size());

		List<ServiceDirectoryInfo> directoryInfos = ServiceInfoDao.selectServiceDirectory();
		Assert.assertEquals(2, directoryInfos.size());
	}

	/**
	 * <serviceDirectory class="team.balam.exof.test.TestService" path="/test">
		 <serviceVariable serviceName="schedule">
			 <variable name="a" value="a1"/>
			 <variable name="b" value="b2"/>
			 <variable name="c" value="c3"/>
		 </serviceVariable>
		 <serviceVariable serviceName="arrayParam">
			 <variable name="a" value="a1"/>
			 <variable name="b" value="b2"/>
			 <variable name="c" value="c1"/>
			 <variable name="c" value="c2"/>
			 <variable name="c" value="c3"/>
			 <variable name="c" value="c4"/>
		 </serviceVariable>
	 </serviceDirectory>
	 <serviceDirectory class="team.balam.exof.test.TestService" path="/test2">
		 <serviceVariable serviceName="schedule">
			 <variable name="a" value="a1"/>
			 <variable name="b" value="b2"/>
			 <variable name="c" value="c3"/>
		 </serviceVariable>
	 </serviceDirectory>
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void test_serviceVariable() throws Exception {
		ServiceLoader loader = new ServiceLoader();
		loader.load("./env");

		ServiceVariable result = ServiceInfoDao.selectServiceVariable("/test", "schedule");
		Assert.assertEquals("a1", result.getString("a"));
		Assert.assertEquals("b2", result.getString("b"));
		Assert.assertEquals("c3", result.getString("c"));

		result = ServiceInfoDao.selectServiceVariable("/test", "arrayParam");
		Assert.assertEquals("a1", result.getString("a"));
		Assert.assertEquals("b2", result.getString("b"));
		Assert.assertEquals(4, ((List<String>) result.get("c")).size());

		result = ServiceInfoDao.selectServiceVariable("/test2", "schedule");
		Assert.assertEquals("a1", result.getString("a"));
		Assert.assertEquals("b2", result.getString("b"));
		Assert.assertEquals("c3", result.getString("c"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_loadService() throws Exception {
		ServiceLoader loader = new ServiceLoader();
		loader.load("./env");

		ServiceProvider.getInstance().start();

		Service service = ServiceProvider.lookup("/test/schedule");
		Assert.assertEquals("a1", service.getServiceVariable("a"));
		Assert.assertEquals("b2", service.getServiceVariable("b"));
		Assert.assertEquals("c3", service.getServiceVariable("c"));

		service = ServiceProvider.lookup("/test/arrayParam");
		Assert.assertEquals("a1", service.getServiceVariable("a"));
		Assert.assertEquals("b2", service.getServiceVariable("b"));
		Assert.assertEquals(4, ((List<String>) service.getServiceVariable("c")).size());

		service = ServiceProvider.lookup("/test2/schedule");
		Assert.assertEquals("a1", service.getServiceVariable("a"));
		Assert.assertEquals("b2", service.getServiceVariable("b"));
		Assert.assertEquals("c3", service.getServiceVariable("c"));
	}

	@Test
	public void test_loadScheduler() throws Exception {
		new ServiceLoader().load("./env");;
		new FrameworkLoader().load("./env");

		SchedulerManager.getInstance().start();

		Assert.assertEquals(2, SchedulerManager.getInstance().getScheduleList().size());
	}
}
