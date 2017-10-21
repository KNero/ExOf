package balam.exof.test.module.deploy;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import team.balam.exof.Constant;
import team.balam.exof.client.Client;
import team.balam.exof.client.DefaultClient;
import team.balam.exof.module.deploy.DeployRequester;

import java.io.File;
import java.nio.charset.Charset;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeployRequesterTest {
	@Test
	public void test01_sendTestJar() throws Exception {
		DeployRequester requester = new DeployRequester("localhost", 3002);
		requester.setId("test");
		requester.setPassword("P@ssword");
		requester.sendExternalLib(new File("./lib/ext2/ext1.jar"));
	}

	@Test
	public void test02_deployService() throws Exception {
		DeployRequester requester = new DeployRequester("localhost", 3002);
		requester.setId("test");
		requester.setPassword("P@ssword");
		requester.reloadService();
	}

	@Test
	public void test03_sendExternalService() throws Exception {
		Client client = new DefaultClient(_socketChannel ->
				new ChannelHandler[]{new StringEncoder(),
						new DelimiterBasedFrameDecoder(2048, Delimiters.nulDelimiter()),
						new StringDecoder(Charset.forName(Constant.NETWORK_CHARSET))});

		client.connect("localhost", 2000);

		String res = client.sendAndWait("{\"aaa\":\"external_test\", \"servicePath\":\"/test/external/execute\"}\0");
		client.close();

		Assert.assertEquals("response-2-2", res);
	}

	@Test
	public void test04_sendTestJar() throws Exception {
		DeployRequester requester = new DeployRequester("localhost", 3002);
		requester.setId("test");
		requester.setPassword("P@ssword");
		requester.sendExternalLib(new File("./lib/ext1/ext1.jar"));
	}

	@Test
	public void test05_deployService() throws Exception {
		DeployRequester requester = new DeployRequester("localhost", 3002);
		requester.setId("test");
		requester.setPassword("P@ssword");
		requester.reloadService();
	}

	@Test
	public void test06_sendExternalService() throws Exception {
		Client client = new DefaultClient(_socketChannel ->
				new ChannelHandler[]{new StringEncoder(),
						new DelimiterBasedFrameDecoder(2048, Delimiters.nulDelimiter()),
						new StringDecoder(Charset.forName(Constant.NETWORK_CHARSET))});

		client.connect("localhost", 2000);

		String res = client.sendAndWait("{\"aaa\":\"external_test\", \"servicePath\":\"/test/external/execute\"}\0");
		client.close();

		Assert.assertEquals("response-1-1", res);
	}
}
