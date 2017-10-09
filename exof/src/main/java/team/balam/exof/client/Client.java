package team.balam.exof.client;

import java.io.Closeable;
import java.io.IOException;

public interface Client extends Closeable {
	void connect(String _host, int _port) throws IOException;
	
	void setReadTimeout(int _timeout);
	
	void setConnectTimeout(int _timeout);
	
	void send(Object _data) throws Exception;

	void flush();

	ResponseFuture getResponse();

	/**
	 * 요청을 보내고 flush() 호출 후 응답을 기다린다
	 * @param _data 요청 데이터
	 * @param <T> 받을 응답의 타입
	 * @return getResponse().get() 과 같다
	 * @throws Exception
	 */
	<T> T sendAndWait(Object _data) throws Exception;
}
