package team.balam.exof.client;

import java.io.IOException;

import io.netty.channel.ChannelHandler;

public class SynchClient<I, O> extends AbstractClient<I, O> 
{
	public SynchClient(ChannelHandler[] _channelHandler) 
	{
		super(_channelHandler);
	}
	
	@Override
	public ResponseFuture<O> send(I _data) throws IOException
	{
		return null;
	}
}