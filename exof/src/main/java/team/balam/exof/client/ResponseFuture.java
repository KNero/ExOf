package team.balam.exof.client;

public interface ResponseFuture<V>
{
	void await(long timeoutMillis) throws InterruptedException;
	
	boolean isSuccess();
	
	V get();
	
	Throwable cause();
}
