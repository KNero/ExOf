package team.balam.exof.client;

import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ResponseFutureImpl<O> extends ChannelInboundHandlerAdapter implements ResponseFuture<O>
{
	private O response;
	private Exception exception;
	
	@SuppressWarnings("unchecked")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		this.response = (O)msg;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		if(cause instanceof Exception)
		{
			this.exception = (Exception)cause;
		}
		else
		{
			this.exception = new Exception(cause);
		}
	}
	
	@Override
	public void await(long _timeoutMillis)
	{
		long start = System.currentTimeMillis();
		
		while(true)
		{
			if(this.response != null || this.exception != null)
			{
				break;
			}
			else if(System.currentTimeMillis() - start >= _timeoutMillis)
			{
				this.exception = new IOException("Read Timeout.");
				break;
			}
			
			try
			{
				Thread.sleep(1);
			}
			catch(InterruptedException e) 
			{
			}
		}
	}
	
	@Override
	public boolean isSuccess()
	{
		return this.response != null;
	}

	@Override
	public O get()
	{
		return this.response;
	}

	@Override
	public Exception cause()
	{
		return this.exception;
	}
}
