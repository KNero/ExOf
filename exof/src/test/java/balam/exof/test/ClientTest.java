package balam.exof.test;

import java.nio.charset.Charset;

import org.junit.Test;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import team.balam.exof.ConstantKey;
import team.balam.exof.client.Sender;
import team.balam.exof.container.console.client.InfoGetter;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

public class ClientTest
{
	@Test
	public void getScheduleList()
	{
		InfoGetter getter = new InfoGetter();
		getter.getScheduleList();
	}
	
	@Test
	public void send() throws Exception
	{
		try
		{
			Sender<String, String> client = new Sender<>(new ChannelHandlerMaker(){
				@Override
				public ChannelHandler[] make(SocketChannel _socketChannel) 
				{
					return new ChannelHandler[]{new StringEncoder(), 
							new DelimiterBasedFrameDecoder(2048, Delimiters.nulDelimiter()),
							new StringDecoder(Charset.forName(ConstantKey.NETWORK_CHARSET))};
				}
			});
			
			client.setConnectTimeout(5000);
			client.setReadTimeout(3000);
			client.connect("127.0.0.1", 2000);

			String res = client.sendAndWait("{\"aaa\":\"ababa\", \"servicePath\":\"/test/receive\"}\0");
			System.out.println("============>" + res);
			
			client.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}
