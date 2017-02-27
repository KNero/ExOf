package team.balam.exof.client;

import java.io.Closeable;
import java.io.IOException;

public interface Client<I, O> extends Closeable 
{
	int DEFAULT_CONNECT_TIMEOUT = 3000;
	
	void connect(String _host, int _port) throws IOException;
	
	void setReadTimeout(int _timeout);
	
	void setConnectTimeout(int _timeout);
	
	ResponseFuture<O> send(I _data) throws IOException;
}
