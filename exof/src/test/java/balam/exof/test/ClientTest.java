package balam.exof.test;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.junit.Assert;
import org.junit.Test;
import team.balam.exof.Constant;
import team.balam.exof.client.Sender;
import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ServiceList;
import team.balam.exof.container.console.client.Client;
import team.balam.exof.environment.EnvKey;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

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

	@Test
	@SuppressWarnings("unchecked")
	public void testConsoleGetServiceList() throws Exception {
		Client.init();

		Client.send(new Command(ServiceList.GET_SERVICE_LIST), _successResult -> {
			try {
				Map<String, Object> resultMap = (Map<String, Object>) _successResult;
				resultMap.forEach((_key, _value) -> {
					Map<String, Object> valueMap = (Map<String, Object>) _value;

					Assert.assertNotNull(valueMap.get(Command.Key.CLASS));

					valueMap.keySet().forEach(_valueKey -> {
						if (!Command.Key.CLASS.equals(_valueKey)) {
							if (!_valueKey.endsWith(EnvKey.Service.SERVICE_VARIABLE)) {
								Assert.assertNotNull(valueMap.get(_valueKey));

								Map<String, String> variables = (Map<String, String>) valueMap.get(_valueKey + EnvKey.Service.SERVICE_VARIABLE);
								variables.keySet().forEach(_name -> Assert.assertNotNull(variables.get(_name)));
							}
						}
					});

					System.out.println();
				});
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}, _failResult -> Assert.fail());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testConsoleGetScheduleList() throws Exception {
		Client.init();

		Client.send(new Command(ServiceList.GET_SCHEDULE_LIST), _successResult -> {
			try {
				Map<String, Object> resultMap = (Map<String, Object>) _successResult;
				Assert.assertNotNull(resultMap.get("list"));
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}, _failResult -> Assert.fail());
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
