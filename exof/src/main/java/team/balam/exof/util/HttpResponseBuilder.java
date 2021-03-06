package team.balam.exof.util;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.codehaus.jackson.map.ObjectMapper;
import team.balam.exof.module.listener.RequestContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

public class HttpResponseBuilder
{
	private static ObjectMapper jsonMapper = new ObjectMapper();
	
	public static FullHttpResponse buildOk(Object _content) {
		return createHttpResponse(HttpResponseStatus.OK, _content);
	}

	public static FullHttpResponse buildOkMessage(String _msg) {
		return createHttpResponse(HttpResponseStatus.OK, _msg);
	}

	public static FullHttpResponse buildOkJson(Map<String, Object> data) {
		return createHttpResponse(HttpResponseStatus.OK, data);
	}

	public static FullHttpResponse buildServerError(String _msg) {
		return createHttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, _msg);
	}

	public static FullHttpResponse buildBadRequest(String _msg) {
		return createHttpResponse(HttpResponseStatus.BAD_REQUEST, _msg);
	}

	public static FullHttpResponse buildNotImplemented(String _msg) {
		return createHttpResponse(HttpResponseStatus.NOT_IMPLEMENTED, _msg);
	}

	public static FullHttpResponse buildUnauthorized(String _msg) {
		return createHttpResponse(HttpResponseStatus.UNAUTHORIZED, _msg);
	}
	
	private static FullHttpResponse createHttpResponse(HttpResponseStatus _status, Object _content) {
		String contentStr = null;
		
		if (_content instanceof String) {
			contentStr = (String) _content;
		}
		else if (_content != null) {
			StringWriter strWriter = new StringWriter();

			try {
				jsonMapper.writeValue(strWriter, _content);
				
				contentStr = strWriter.toString();
			} catch (IOException e) {
				e.printStackTrace(new PrintWriter(strWriter));
				contentStr = strWriter.toString();
			}
		}
		
		return createHttpResponse(_status, contentStr);
	}
	
	private static FullHttpResponse createHttpResponse(HttpResponseStatus _status, String _content) {
		FullHttpResponse response;

		if (_content == null) {
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, _status);
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
		} else {
			byte[] contentBuf = _content.getBytes();

			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, _status, Unpooled.copiedBuffer(contentBuf));
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentBuf.length);
		}

		FullHttpRequest request = RequestContext.get(RequestContext.Key.ORIGINAL_REQUEST);
		if (HttpUtil.isKeepAlive(request)) {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}

		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");

		return response;
	}
}
