package team.balam.exof.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ResponseFutureImpl<O> extends ChannelInboundHandlerAdapter implements ResponseFuture<O>
{
	private BlockingQueue<Object> responseQueue;
	private O response;
	private Throwable throwable;
	private boolean isSuccess;
	
	public ResponseFutureImpl()
	{
		this.responseQueue = new ArrayBlockingQueue<>(2);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		this.responseQueue.add(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		this.responseQueue.add(cause);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void await(long _timeoutMillis)
	{
		long start = System.currentTimeMillis();
		
		while(true)
		{
			Object res = this.responseQueue.poll();
			if(res != null)
			{
				if(res instanceof Throwable)
				{
					this.throwable = (Throwable)res;
					this.isSuccess = false;
				}
				else
				{
					this.response = (O)res;
					this.isSuccess = true;
				}
				
				break;
			}
			else if(System.currentTimeMillis() - start >= _timeoutMillis)
			{
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
		return this.isSuccess;
	}

	@Override
	public O get()
	{
		if(this.response == null)
		{
			this.await(0);
		}
		
		return this.response;
	}

	@Override
	public Throwable cause()
	{
		return this.throwable;
	}
}
