package balam.exof.test;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.bytes.ByteArrayDecoder;

import org.junit.Test;

import team.balam.exof.client.Client;
import team.balam.exof.client.SynchClinet;
import team.balam.exof.container.console.client.InfoGetter;

public class ClientTest
{
	@Test
	public void getScheduleList()
	{
		InfoGetter getter = new InfoGetter();
		getter.getScheduleList();
	}
	
	@Test
	public void connect() throws Exception
	{
		try
		{
			SynchClinet client = new SynchClinet(new ChannelHandler[]{new ByteArrayDecoder()});
			client.connect("127.0.0.1", 3001);
			client.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}
