package team.balam.exof.client;

public interface ResponseFuture<V>
{
	void await(long timeoutMillis);
	
	boolean isDone();
	
	V get() throws Exception;
}
