package team.balam.exof.client;

import io.netty.channel.ChannelHandler.Sharable;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

@Sharable
public class Sender<I, O> extends AbstractClient<I, O> 
{
	public Sender(ChannelHandlerMaker _channelHandler) 
	{
		super(_channelHandler);
	}
	
	@Override
	public ResponseFuture<O> send(I _data) throws Exception
	{
		this.channel.writeAndFlush(_data);
		
		return this.getResponse();
	}
	
	@Override
	public O sendAndWait(I _data) throws Exception
	{
		ResponseFuture<O> resFuture = this.send(_data);
		resFuture.await(this.readTimeout);
		
		return resFuture.get();
	}
}