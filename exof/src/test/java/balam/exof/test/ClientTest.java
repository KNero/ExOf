package balam.exof.test;

import java.nio.charset.Charset;

import org.junit.Test;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import junit.framework.Assert;
import team.balam.exof.Constant;
import team.balam.exof.client.Sender;
import team.balam.exof.container.console.CommandBuilder;
import team.balam.exof.container.console.client.Client;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

public class ClientTest
{
	@Test
	public void testSender() throws Exception
	{
		Sender<String, String> client = new Sender<>(new ChannelHandlerMaker(){
			@Override
			public ChannelHandler[] make(SocketChannel _socketChannel) 
			{
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
	public void testConsoleGetServiceList() throws Exception
	{
		Client.init();
		
		Client.send(CommandBuilder.buildServiceListGetter(), _result -> {
			
		});
	}
	
	@Test
	public void testConsoleGetScheduleList() throws Exception
	{
		Client.init();
		
		Client.send(CommandBuilder.buildScheduleListGetter(), _result -> {
			
		});
	}
}
