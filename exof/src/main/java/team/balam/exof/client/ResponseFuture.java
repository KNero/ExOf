package team.balam.exof.client;

public interface ResponseFuture<V>
{
	void await(long timeoutMillis);
	
	boolean isSuccess();
	
	V get();
	
	Exception cause();
}
