package team.balam.exof.module.group;

import java.io.Closeable;
import java.io.IOException;

public interface Session extends Closeable
{
	void connect() throws IOException;
	
	void send(byte[] _data) throws IOException;
}
