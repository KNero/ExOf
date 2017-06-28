package balam.exof.test;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import team.balam.exof.Constant;
import team.balam.exof.client.Sender;
import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ServiceList;
import team.balam.exof.container.console.client.Client;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

public class ClientTest {
	@Test
	public void testSender() throws Exception {
		Sender<String, String> client = new Sender<>(new ChannelHandlerMaker() {
			@Override
			public ChannelHandler[] make(SocketChannel _socketChannel) {
				return new ChannelHandler[]{new StringEncoder(),
						new DelimiterBasedFrameDecoder(2048, Delimiters.nulDelimiter()),
						new StringDecoder(Charset.forName(Constant.NETWORK_CHARSET))};
			}
		});

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

		Client.send(new Command(ServiceList.SHOW_SERVICE_LIST), _successResult -> {
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
								variables.keySet().forEach(_name -> {
									Assert.assertNotNull(variables.get(_name));
								});
							}
						}
					});

					System.out.println();
				});
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}, _failResult -> {
			Assert.fail();
		});
	}

	@Test
	public void testConsoleGetScheduleList() throws Exception {
		Client.init();

		Client.send(new Command(ServiceList.SHOW_SCHEDULE_LIST), _successResult -> {
			try {
				Map<String, Object> resultMap = (Map<String, Object>) _successResult;
				Assert.assertNotNull(resultMap.get("list"));
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}, _failResult -> {
			Assert.fail();
		});
	}

	@Test
	public void test_ConsoleGetDynamicSettingList() throws Exception {
		Client.init();

		Client.send(new Command(ServiceList.SHOW_DYNAMIC_SETTING_LIST), _successResult -> {
			try {
				Assert.assertNotEquals(0, ((List<Object>) _successResult).size());
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}, _failResult -> {
			Assert.fail();
		});
	}
}
