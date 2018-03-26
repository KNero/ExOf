package team.balam.exof.module.listener;

import java.util.HashMap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestContext 
{
	public enum Key {
		CHANNEL_CONTEXT, SERVICE_OBJECT, ORIGINAL_REQUEST, HTTP_SERVLET_REQ, HTTP_SERVLET_RES
	}
	
	private static ThreadLocal<HashMap<String, Object>> context = new ThreadLocal<HashMap<String, Object>>();
	
	public static void set(Key key, Object obj) {
		HashMap<String, Object> c = context.get();
		if (c == null) {
			c = new HashMap<>();
			context.set(c);
		}

		c.put(key.name(), obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Key key) {
		HashMap<String, Object> c =  context.get();
		return (T) c.get(key.name());
	}

	public static HttpServletRequest getServletRequest() {
		HttpServletRequest servletRequest = get(Key.HTTP_SERVLET_REQ);
		if (servletRequest == null) {
			throw new NullPointerException("Servlet request is null");
		}

		return servletRequest;
	}

	public static HttpServletResponse getServletResponse() {
		HttpServletResponse servletResponse = get(Key.HTTP_SERVLET_RES);
		if (servletResponse == null) {
			throw new NullPointerException("Servlet response is null");
		}
		return servletResponse;
	}

	public static ChannelFuture writeResponse(Object res) {
		ChannelHandlerContext channelContext = get(Key.CHANNEL_CONTEXT);
		if (channelContext != null) {
			if (res instanceof byte[]) {
				byte[] bytes = (byte[]) res;

				ByteBuf buf = channelContext.alloc().buffer(bytes.length);
				buf.writeBytes(bytes);

				return channelContext.write(buf);
			} else {
				return channelContext.write(res);
			}
		} else {
			throw new NullPointerException("netty session is null.");
		}
	}

	public static ChannelFuture writeAndFlushResponse(Object res) {
		ChannelHandlerContext channelContext = get(Key.CHANNEL_CONTEXT);
		if (channelContext != null) {
			if (res instanceof byte[]) {
				byte[] bytes = (byte[]) res;

				ByteBuf buf = channelContext.alloc().buffer(bytes.length);
				buf.writeBytes(bytes);

				return channelContext.writeAndFlush(buf);
			} else {
				return channelContext.writeAndFlush(res);
			}
		} else {
			throw new NullPointerException("netty session is null.");
		}
	}
}
