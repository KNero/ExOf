package balam.exof.test;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import team.balam.exof.Constant;
import team.balam.exof.client.Sender;
import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ConsoleCommandHandler;
import team.balam.exof.container.console.ServiceList;
import team.balam.exof.container.console.client.Client;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.util.sqlite.connection.DatabaseLoader;
import team.balam.util.sqlite.connection.pool.AlreadyExistsConnectionException;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class ClientTest {

	@Test
	public void testSender() throws Exception {
		Sender<String, String> client = new Sender<>(_socketChannel ->
			new ChannelHandler[]{new StringEncoder(),
					new DelimiterBasedFrameDecoder(2048, Delimiters.nulDelimiter()),
					new StringDecoder(Charset.forName(Constant.NETWORK_CHARSET))});

		client.setConnectTimeout(5000);
		client.setReadTimeout(3000);
		client.connect("localhost", 2000);

		String res = client.sendAndWait("{\"aaa\":\"ababa\", \"servicePath\":\"/test/receive\"}\0");
		client.close();

		Assert.assertEquals(res, "response");
	}

	@BeforeClass
	public static void init() throws Exception {
		try {
			DatabaseLoader.load(Constant.ENV_DB, "./env/" + Constant.ENV_DB);
		} catch (AlreadyExistsConnectionException e) {
		}

		ServiceProvider.getInstance().start();

		Client.init();

		Command command = new Command(ServiceList.ADD_DYNAMIC_SETTING);
		command.addParameter(Command.Key.NAME, "name0");
		command.addParameter(Command.Key.VALUE, "value0");
		command.addParameter(Command.Key.DESCRIPTION, "des0");
		Client.send(command, System.out::println, null);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_consoleGetServiceList() throws Exception {
		ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
		Mockito.when(ctx.writeAndFlush(Mockito.any())).thenAnswer(object -> {
			String jsonStr = object.getArgumentAt(0, String.class);
			TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>() {};

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> resultMap = objectMapper.readValue(jsonStr, mapType);

			resultMap.forEach((_key, _value) -> {
				Map<String, Object> valueMap = (Map<String, Object>) _value;

				StringBuilder infoLog = new StringBuilder();
				Assert.assertNotNull(valueMap.get(Command.Key.CLASS));

				infoLog.append("Directory path : ").append(_key).append("\n");
				infoLog.append("Class : ").append(valueMap.get(Command.Key.CLASS)).append("\n");
				infoLog.append("Service list").append("\n");

				valueMap.keySet().forEach(_valueKey -> {
					if (!Command.Key.CLASS.equals(_valueKey)) {
						if (!_valueKey.endsWith(EnvKey.Service.SERVICE_VARIABLE)) {
							Assert.assertNotNull(valueMap.get(_valueKey));

							infoLog.append(" -s- ").append(_valueKey).append("(method name : ").append(valueMap.get(_valueKey)).append(")").append("\n");

							Map<String, Object> variables = (Map<String, Object>) valueMap.get(_valueKey + EnvKey.Service.SERVICE_VARIABLE);
							variables.keySet().forEach(_name -> Assert.assertNotNull(variables.get(_name)));
							variables.keySet().forEach(_name -> infoLog.append("   -v- ").append(_name).append(" : ").append(variables.get(_name).toString()).append("\n"));
						}
					}
				});

				System.out.println(infoLog);
			});
			return null;
		});

		Command command = new Command(ServiceList.GET_SERVICE_LIST);
		ConsoleCommandHandler handler = new ConsoleCommandHandler();
		handler.channelRead(ctx, command.toJson());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testConsoleGetScheduleList() throws Exception {
		ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
		Mockito.when(ctx.writeAndFlush(Mockito.any())).thenAnswer(object -> {
			String jsonStr = object.getArgumentAt(0, String.class);
			TypeReference<List<Object>> listType = new TypeReference<List<Object>>() {};

			ObjectMapper objectMapper = new ObjectMapper();
			List<Object> resultList = objectMapper.readValue(jsonStr, listType);

			Assert.assertEquals(2, resultList.size());
			resultList.forEach(System.out::println);
			return null;
		});

		Command command = new Command(ServiceList.GET_SCHEDULE_LIST);
		ConsoleCommandHandler handler = new ConsoleCommandHandler();
		handler.channelRead(ctx, command.toJson());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_ConsoleGetDynamicSettingList() throws Exception {
		Client.init();

		Client.send(new Command(ServiceList.GET_DYNAMIC_SETTING_LIST), _successResult -> {
			try {
				Assert.assertNotEquals(0, ((List<Object>) _successResult).size());
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}, _failResult -> Assert.fail());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_ConsoleGetDynamicSettingSingle() throws Exception {
		Client.init();

		Command command = new Command(ServiceList.GET_DYNAMIC_SETTING_LIST);
		command.addParameter(Command.Key.NAME, "name0");

		Client.send(command, _successResult -> {
			try {
				Assert.assertEquals(1, ((List<Object>) _successResult).size());
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}, _failResult -> Assert.fail());
	}

	@Test
	public void test_consoleSetServiceVariable() throws Exception {
		Client.init();

		Command command = new Command(ServiceList.SET_SERVICE_VARIABLE_VALUE);
		command.addParameter(Command.Key.SERVICE_PATH, "/test/schedule");
		command.addParameter(Command.Key.VARIABLE_NAME, "a");
		command.addParameter(Command.Key.VARIABLE_VALUE, "aaaaa1111");

		Client.send(command, _result -> {
			try {
				Assert.assertEquals("aaaaa1111", _result);
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}, _failResult -> Assert.fail());
	}
}
