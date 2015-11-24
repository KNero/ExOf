package balam.exof.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil
{
	public static byte[] read(InputStream _in, int _length) throws IOException
	{
		ByteArrayOutputStream resultBuf = new ByteArrayOutputStream(_length);
		byte[] buf = new byte[_length];
		int read, remain = 0;
		
		while((read = _in.read(buf)) != -1)
		{
			resultBuf.write(buf, 0, read);
			
			remain = _length - read;
			if(remain > 0) buf = new byte[remain];
			else break;
		}
		
		return resultBuf.toByteArray();
	}
}
