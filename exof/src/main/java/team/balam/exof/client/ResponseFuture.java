package team.balam.exof.client;

public interface ResponseFuture {
	void await(long timeoutMillis);
	
	boolean isDone();
	
	<T> T get() throws Exception;
}
