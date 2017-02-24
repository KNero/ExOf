package team.balam.exof.client;

import io.netty.channel.ChannelHandler;

import java.io.IOException;

public class SynchClinet extends AbstractClient 
{
	public SynchClinet(ChannelHandler[] _channelHandler) 
	{
		super(_channelHandler);
	}

	@Override
	public Object send(Object _data) throws IOException 
	{
		return null;
	}
}