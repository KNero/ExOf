package team.balam.exof.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ResponseFutureImpl<O> extends ChannelInboundHandlerAdapter implements ResponseFuture<O>
{
	private BlockingQueue<Object> responseQueue;
	
	public ResponseFutureImpl()
	{
		this.responseQueue = new LinkedBlockingQueue<>();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext _ctx, Object _msg) throws Exception
	{
		this.responseQueue.add(_msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		if(cause instanceof Exception)
		{
			this.responseQueue.add(cause);
		}
		else
		{
			this.responseQueue.add(new Exception(cause));
		}
	}
	
	@Override
	public void await(long _timeoutMillis)
	{
		long start = System.currentTimeMillis();
		
		while(true)
		{
			if(this.responseQueue.size() > 0)
			{
				break;
			}
			else if(System.currentTimeMillis() - start >= _timeoutMillis)
			{
				this.responseQueue.add(new IOException("Read Timeout."));
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
	public boolean isDone()
	{
		return this.responseQueue.size() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public O get() throws Exception
	{
		Object res = this.responseQueue.poll();
		if(res != null)
		{
			if(res instanceof Exception)
			{
				throw (Exception)res;
			}
			else
			{
				return (O)res;
			}
		}
		
		return null;
	}
}
