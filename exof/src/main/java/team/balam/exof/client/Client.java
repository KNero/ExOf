package team.balam.exof.client;

import java.io.Closeable;
import java.io.IOException;

public interface Client extends Closeable 
{
	int DEFAULT_CONNECT_TIMEOUT = 3000;
	
	void connect(String _host, int _port) throws IOException;
	
	void setReadTimeout(int _time);
	
	void setConnectTimeout(int _time);
	
	Object send(Object _data) throws IOException;
}
