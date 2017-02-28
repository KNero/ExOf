package team.balam.exof.client;

import io.netty.channel.ChannelHandler.Sharable;

import java.io.IOException;

import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

@Sharable
public class SynchClient<I, O> extends AbstractClient<I, O> 
{
	public SynchClient(ChannelHandlerMaker _channelHandler) 
	{
		super(_channelHandler);
	}
	
	@Override
	public ResponseFuture<O> send(I _data) throws IOException
	{
		this.channel.writeAndFlush(_data);
		
		ResponseFuture<O> resFuture = this.getResponse();
		
		try
		{
			resFuture.await(this.readTimeout);
			
			if(resFuture.get() != null)
			{
				return resFuture;
			}
			else
			{
				throw new IOException("Read Timeout.");
			}
		}
		catch(Exception e)
		{
			throw new IOException("Can not wait response.", e);
		}
	}
}