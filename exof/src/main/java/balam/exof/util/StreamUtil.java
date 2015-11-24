package balam.exof.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil
{
	/**
	 * InputStream 중 원하는 길이만큼 데이터를 읽는다.
	 * @param _in 대상 InputStream
	 * @param _length 읽어야할 데이터 길이
	 * @return 읽어들인 데이터 길이
	 * @throws IOException
	 */
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
