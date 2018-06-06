package team.balam.exof;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import team.balam.exof.client.ClientPool;
import team.balam.exof.client.DefaultClient;
import team.balam.exof.client.component.HttpClientCodec;
import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.ConsoleCommandHandler;
import team.balam.exof.container.console.ServiceList;
import team.balam.exof.container.console.client.Client;
import team.balam.exof.db.ListenerDao;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.ServiceLoader;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.util.sqlite.connection.DatabaseLoader;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class ClientTest {
	private static final String TEST_HOME = "./test-workspace/ClientTest";
	private static final String TEST_DB = TEST_HOME + Constant.SERVICE_SEPARATE + Constant.ENV_DB;

	@BeforeClass
	public static void init() throws Exception {
		new File(TEST_DB).delete();
		try {
			DatabaseLoader.load(Constant.ENV_DB, TEST_DB);
		} catch (Exception e) {
			//ignore
		}
		ExternalClassLoader.load("./lib/external");

		ServiceInfoDao.initTable();
		ListenerDao.initTable();
		new ServiceLoader().load(TEST_HOME);
		ServiceProvider.getInstance().start();
	}

	@Test
	public void test_sendJson() throws Exception {
		team.balam.exof.client.Client client = new DefaultClient(_socketChannel ->
			new ChannelHandler[]{new StringEncoder(),
					new DelimiterBasedFrameDecoder(2048, Delimiters.nulDelimiter()),
					new StringDecoder(Charset.forName(Constant.NETWORK_CHARSET))});

		client.connect("localhost", 2000);

		String res = client.sendAndWait("{\"aaa\":\"ababa\", \"servicePath\":\"/test/receive\"}\0");
		client.close();

		Assert.assertEquals(res, "response");
	}

	@Test
	public void test_sendHttp() throws Exception {
		URI uri = new URI("/test/receiveHttp?message=response");
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
		request.headers().set(HttpHeaderNames.HOST, "localhost");
		request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

		team.balam.exof.client.Client sender = new DefaultClient(new HttpClientCodec(1048576));
		sender.setConnectTimeout(3000);

		sender.connect("localhost", 2001);
		FullHttpResponse response = sender.sendAndWait(request);
		sender.close();

		Assert.assertEquals("response", response.content().toString(CharsetUtil.UTF_8));
	}

	@Test
	public void test_sendHttpByPool() throws Exception {
		URI uri = new URI("/test/receiveHttp?message=response");
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
		request.headers().set(HttpHeaderNames.HOST, "localhost");
		request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

		ClientPool clientPool = new ClientPool.Builder()
				.addTarget("127.0.0.1", 2001)
				.setMaxPoolSize(2)
				.setReadTimeout(5000)
				.setAcquireTimeout(100000)
				.setChannelHandlerMaker(new HttpClientCodec())
				.build();

		team.balam.exof.client.Client sender = clientPool.get();
		FullHttpResponse response = sender.sendAndWait(request);
		sender.close();

		Thread.sleep(1);
		Assert.assertEquals("response", response.content().toString(CharsetUtil.UTF_8));
	}

	@Test
	public void test_addDynamic() throws Exception {
		Command command = new Command(ServiceList.ADD_DYNAMIC_SETTING);
		command.addParameter(Command.Key.NAME, "name0");
		command.addParameter(Command.Key.VALUE, "value0");
		command.addParameter(Command.Key.DESCRIPTION, "des0");
		Client.init();
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

			resultMap.forEach((key, value) -> {
				Map<String, Object> info = (Map<String, Object>) value;
				Assert.assertNotNull(info.get(EnvKey.Service.CLASS));
				Assert.assertNotNull(info.get(EnvKey.Service.SERVICE_VARIABLE));
				Assert.assertNotNull(info.get("services"));
			});
			return null;
		});

		Command command = new Command(ServiceList.GET_SERVICE_LIST);
		ConsoleCommandHandler handler = new ConsoleCommandHandler();
		handler.channelRead(ctx, command.toJson());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_ConsoleGetScheduleList() throws Exception {
		ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
		Mockito.when(ctx.writeAndFlush(Mockito.any())).thenAnswer(object -> {
			String jsonStr = object.getArgumentAt(0, String.class);
			TypeReference<List<Object>> listType = new TypeReference<List<Object>>() {};

			ObjectMapper objectMapper = new ObjectMapper();
			List<Object> resultList = objectMapper.readValue(jsonStr, listType);

			Assert.assertEquals(3, resultList.size());
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
		command.addParameter(Command.Key.VARIABLE_NAME, "scheduleA");
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

//	@Test
	public void test_clientPoolStress() throws Exception {
		final ClientPool clientPool = new ClientPool.Builder()
				.addTarget("localhost", 2000)
				.addTarget("localhost", 2001)
				.setMaxPoolSize(5)
				.setReadTimeout(5000)
				.setAcquireTimeout(100000)
				.setChannelHandlerMaker(new HttpClientCodec())
				.setClientHealthChecker(_client -> {
					try {
						System.out.println("check target health!!");

						String message = UUID.randomUUID().toString();
						URI uri = new URI("/test/receiveHttp?message=" + message);
						HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());

						FullHttpResponse response = _client.sendAndWait(request);
						ByteBuf buf = response.content();
						String resMsg = buf.toString(CharsetUtil.UTF_8);
						buf.release();

						if (message.equals(resMsg)) {
							System.out.println("check target health OK!!");
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				})
				.build();

		int threadCount = 30;
		int sendCount = 1000;
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; ++i) {
			new Thread(() -> {
				for (int j = 0; j < sendCount; ++j) {
					team.balam.exof.client.Client sender = null;

					try {
						String message = UUID.randomUUID().toString();
						URI uri = new URI("/test/receiveHttp?message=" + message);
						HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());

						sender = clientPool.get();
						FullHttpResponse response = sender.sendAndWait(request);
						ByteBuf buf = response.content();
						String resMsg = buf.toString(CharsetUtil.UTF_8);
						buf.release();

						Assert.assertEquals(message, resMsg);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (sender != null) {
							try {
								sender.close();
							} catch (Exception e) {
								//ignore
							}
						}
					}
				}
				latch.countDown();
			}).start();
		}

		latch.await();
		clientPool.destroy();
	}
}
