package team.balam.exof.util;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Runtime.getInstance().exec()를 통해서 os로 명령어를 보내고
 * 응답을 받아온다.
 * @author kwonsm
 *
 */
public class CommandUtil 
{
	private static final int NORMAL_TERMINATION = 0;
	
	private static final Runtime RUNTIME = Runtime.getRuntime();
	
	/**
	 * curl GET을 호출 한다.
	 * <br>
	 * See : <a href="https://curl.haxx.se/docs/manpage.html#-m">https://curl.haxx.se/docs/manpage.html#-m</a>
	 * @param _host 연결할 대상의 주소.
	 * @param _connectTimeout 연결을 기다리는 시간.
	 * @param _maxTimeout 전체 수행 시간.
	 * @param _maxResLength 응답 메시지 길이(0 : 무제한)
	 * @param _charset 응답의 케릭터셋.
	 * @return String 으로 변환된 응답.
	 * @throws IOException curl을 실행할 수 없거나 timeout에러가 발생했을 경우.
	 */
	public static String invokeCurlGet(String _host, int _connectTimeout, int _maxTimeout, int _maxResLength, Charset _charset) throws IOException
	{
		byte[] res = execute("curl --connect-timeout " + _connectTimeout + " --max-time " + _maxTimeout + " -X GET " + _host, _maxTimeout, _maxResLength);
		
		return new String(res, _charset);
	}
	
	public static byte[] execute(String _cmd, int _maxTimeout, int _maxResLength) throws IOException
	{
		Process process = RUNTIME.exec(_cmd);
		
		try
		{
			boolean isSuccess = process.waitFor(_maxTimeout, TimeUnit.SECONDS);
			if(! isSuccess)
			{
				
				throw new IOException("Command execute timeout.");
			}
			else if(process.exitValue() != NORMAL_TERMINATION)
			{
				throw new IOException("Fail to execute cammand. Exit Value[" + process.exitValue() + "], cmd => " + _cmd);
			}
		}
		catch(InterruptedException e)
		{
			process.destroyForcibly();
			
			throw new IOException(e);
		}

		BufferedInputStream in = null;
		
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			in = new BufferedInputStream(process.getInputStream());
			byte[] buf = new byte[1024];
			int read = 0;
			
			while((read = in.read(buf)) != -1)
			{
				out.write(buf, 0, read);
				out.flush();
				
				if(_maxResLength > 0 && out.size() > _maxResLength)
				{
					throw new IOException("Response length exceeded.");
				}
			}
			
			return out.toByteArray();
		}
		finally
		{
			if(in != null)
			{
				in.close();
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println(invokeCurlGet("http://www.daum.net", 5, 10, 0, Charset.defaultCharset()));
	}
}
