package team.balam.exof.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ResponseFutureImpl<O> implements ResponseFuture<O>
{
	private BlockingQueue<Object> responseQueue;
	private O response;
	private Throwable throwable;
	private boolean isSuccess;
	
	public ResponseFutureImpl(BlockingQueue<Object> _responseQueue)
	{
		this.responseQueue = _responseQueue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void await(long timeoutMillis) throws InterruptedException
	{
		Object res = this.responseQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
		
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
	}
	
	@Override
	public boolean isSuccess()
	{
		return this.isSuccess;
	}

	@Override
	public O get()
	{
		return this.response;
	}

	@Override
	public Throwable cause()
	{
		return this.throwable;
	}
}
