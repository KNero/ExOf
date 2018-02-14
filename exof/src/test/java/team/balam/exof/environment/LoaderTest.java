package team.balam.exof.environment;

import io.netty.channel.ChannelHandlerContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.TestInitializer;
import team.balam.exof.container.SchedulerManager;
import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ConsoleCommandHandler;
import team.balam.exof.container.console.ServiceList;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.vo.SchedulerInfo;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.environment.vo.ServiceVariable;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.test.OneService;
import team.balam.exof.test.TestService;

import java.util.List;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoaderTest {
	private static final Logger LOG = LoggerFactory.getLogger(LoaderTest.class);

	@BeforeClass
	public static void init() throws Exception {
		TestInitializer.init();

		SystemSetting.setFramework(EnvKey.Framework.AUTORELOAD_SERVICE_VARIABLE, true);
		new ServiceLoader().load("./env");
		ServiceProvider.getInstance().start();
    }

	@Test
	public void test01_getFrameworkExternal() throws Exception {
		new FrameworkLoader().load("./env");
		Map<String, Object> extMap = SystemSetting.getExternal();
		Assert.assertEquals("abcde", extMap.get("test"));
	}

	@Test
	public void test02_scheduleAndServiceDirectory() {
		List<SchedulerInfo> schedulerInfos = ServiceInfoDao.selectScheduler();
		Assert.assertEquals(3, schedulerInfos.size());

		List<ServiceDirectoryInfo> directoryInfos = ServiceInfoDao.selectServiceDirectory();
		Assert.assertEquals(5, directoryInfos.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test03_serviceVariable() {
		ServiceVariable result = ServiceInfoDao.selectServiceVariable("/test", "schedule");
		LOG.info(result.toString());
		Assert.assertEquals("scheduleA-1", result.getString("scheduleA"));
		Assert.assertEquals("scheduleB-2", result.getString("scheduleB"));
		Assert.assertEquals("scheduleC-3", result.getString("scheduleC"));

		result = ServiceInfoDao.selectServiceVariable("/test", "arrayParam");
		LOG.info(result.toString());
		Assert.assertEquals("arrayParamA-1", result.getString("arrayParamA"));
		Assert.assertEquals("arrayParamB-2", result.getString("arrayParamB"));
		Assert.assertEquals(4, ((List<String>) result.get("arrayParamC")).size());

		result = ServiceInfoDao.selectServiceVariable("/test2", "schedule");
		LOG.info(result.toString());
		Assert.assertEquals("other-scheduleA-a1", result.getString("scheduleA"));
		Assert.assertEquals("other-scheduleB-b2", result.getString("scheduleB"));
		Assert.assertEquals("other-scheduleC-c3", result.getString("scheduleC"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test04_loadService() throws Exception {
		SystemSetting.setFramework(EnvKey.Framework.AUTORELOAD_SERVICE_VARIABLE, true);

		ServiceWrapper service = ServiceProvider.lookup("/test/schedule");
		TestService host = service.getHost();
		Assert.assertEquals("scheduleA-1", host.scheduleA);
		Assert.assertEquals("scheduleB-2", host.scheduleB);
		Assert.assertEquals("scheduleC-3", host.scheduleC);

		service = ServiceProvider.lookup("/test/arrayParam");
		host = service.getHost();
		Assert.assertEquals("arrayParamA-1", host.arrayParamA);
		Assert.assertEquals("arrayParamB-2", host.arrayParamB);
		Assert.assertEquals("arrayParamC-1", host.arrayParamC.get(0));
		Assert.assertEquals("arrayParamC-2", host.arrayParamC.get(1));
		Assert.assertEquals("arrayParamC-3", host.arrayParamC.get(2));
		Assert.assertEquals("arrayParamC-4", host.arrayParamC.get(3));

		service = ServiceProvider.lookup("/test2/schedule");
		host = service.getHost();
		Assert.assertEquals("other-scheduleA-a1", host.scheduleA);
		Assert.assertEquals("other-scheduleB-b2", host.scheduleB);
		Assert.assertEquals("other-scheduleC-c3", host.scheduleC);

		service = ServiceProvider.lookup("/autoScan/autoSchedule");
		service.call(new ServiceObject("/autoScan/autoSchedule"));
	}

	@Test
	public void test05_reloadServiceVariable() throws Exception {
		Command command = new Command(ServiceList.SET_SERVICE_VARIABLE_VALUE);
		command.addParameter(Command.Key.SERVICE_PATH, "/test/schedule");
		command.addParameter(Command.Key.VARIABLE_NAME, "scheduleA");
		command.addParameter(Command.Key.VARIABLE_VALUE, "a2a2");

		ConsoleCommandHandler handler = new ConsoleCommandHandler();
		handler.channelRead(Mockito.mock(ChannelHandlerContext.class), command.toJson());

		ServiceWrapper service = ServiceProvider.lookup("/test/schedule");
		TestService host = service.getHost();
		Assert.assertEquals("a2a2", host.scheduleA);
	}

	@Test
	public void test06_reloadSchedulerOnOff() throws Exception {
		ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
		Mockito.when(ctx.writeAndFlush(Mockito.any())).thenAnswer(object -> {
			String jsonStr = object.getArgumentAt(0, String.class);
			TypeReference<List<Object>> listType = new TypeReference<List<Object>>() {};

			ObjectMapper objectMapper = new ObjectMapper();
			List<Object> resultList = objectMapper.readValue(jsonStr, listType);

			Assert.assertEquals(2, resultList.size());
			resultList.forEach(info -> {
				String infoStr = (String) info;
				if (infoStr.startsWith("ID:test-schedule-01") && infoStr.contains("use:yes")) {
					Assert.fail("use is not value(no)");
				}
			});
			return null;
		});

		SystemSetting.setFramework(EnvKey.Framework.AUTORELOAD_SCHEDULER, true);
		SchedulerManager.getInstance().start();

		Command command = new Command(ServiceList.SET_SCHEDULER_ON_OFF);
		command.addParameter(Command.Key.ID, "test-schedule-01");
		command.addParameter(Command.Key.VALUE, "no");

		ConsoleCommandHandler handler = new ConsoleCommandHandler();
		handler.channelRead(Mockito.mock(ChannelHandlerContext.class), command.toJson());
	}

	@Test
	public void test07_callOneService() throws Exception {
		ServiceWrapper service = ServiceProvider.lookup("/one-service/testSingleMethod");
		service.call(new ServiceObject("/one-service/testSingleMethod"));
		OneService oneService = service.getHost();

		//서비스가 하나일 경우 빈 값을 입력해서 서비스를 가져올 수 있다.
		Assert.assertEquals("one-1", oneService.a);
		Assert.assertEquals("one-2", oneService.b);
		Assert.assertEquals("one-3-1", oneService.c.get(0));
		Assert.assertEquals("one-3-2", oneService.c.get(1));
		Assert.assertEquals("one-3-3", oneService.c.get(2));
		Assert.assertEquals("one-3-4", oneService.c.get(3));
	}
}
