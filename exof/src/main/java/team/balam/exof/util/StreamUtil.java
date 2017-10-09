package team.balam.exof.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	
	/**
	 * InputStream에서 특정 문자열 전까지 읽어 들인다.
	 * @param _in 대상 InputStream
	 * @param _ch 경계가 되는 특정 문자열
	 * @return 읽어들인 데이터 길이
	 * @throws IOException
	 */
	public static byte[] read(InputStream _in, char _ch) throws IOException
	{
		ByteArrayOutputStream resultBuf = new ByteArrayOutputStream();
		BufferedInputStream bufIn = new BufferedInputStream(_in);
		
		int read = 0;
		
		while((read = bufIn.read()) != -1)
		{
			if(read == _ch)
			{
				break;
			}
			
			resultBuf.write(read);
		}
		
		return resultBuf.toByteArray();
	}

	/**
	 * source 의 데이터를 target 으로 쓴다.
	 * @param _source 읽을 스트림
	 * @param _target 목적지
	 * @throws IOException
	 */
	public static void write(InputStream _source, OutputStream _target) throws IOException {
		BufferedInputStream in = new BufferedInputStream(_source);
		BufferedOutputStream out = new BufferedOutputStream(_target);
		byte[] buf = new byte[4096];

		try {
			int read;
			while ((read = in.read(buf)) != -1) {
				out.write(buf, 0, read);
			}

			out.flush();
		} finally {
			in.close();
			out.close();
		}
	}
}
