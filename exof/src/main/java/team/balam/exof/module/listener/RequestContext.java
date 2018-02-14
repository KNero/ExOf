package team.balam.exof.module.listener;

import java.util.HashMap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class RequestContext 
{
	public static final String CHANNEL_CONTEXT = "c_c";
	public static final String SERVICE_OBJECT = "s_o";
	public static final String ORIGINAL_REQUEST = "o_r";
	
	public static final String HTTP_SERVLET_REQ = "h_s_q";
	public static final String HTTP_SERVLET_RES = "h_s_s";
	
	private static ThreadLocal<HashMap<String, Object>> context = new ThreadLocal<HashMap<String, Object>>();
	
	public static void set(String _key, Object _obj) {
		HashMap<String, Object> c = context.get();
		if (c == null) {
			c = new HashMap<>();
			context.set(c);
		}

		c.put(_key, _obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String _key)
	{
		HashMap<String, Object> c =  context.get();
		return (T)c.get(_key);
	}

	public static ChannelFuture writeResponse(Object _res) {
		ChannelHandlerContext channelContext = get(CHANNEL_CONTEXT);
		if (channelContext != null) {
			if (_res instanceof byte[]) {
				byte[] bytes = (byte[]) _res;

				ByteBuf buf = channelContext.alloc().buffer(bytes.length);
				buf.writeBytes(bytes);

				return channelContext.write(buf);
			} else {
				return channelContext.write(_res);
			}
		} else {
			throw new NullPointerException("Session");
		}
	}

	public static ChannelFuture writeAndFlushResponse(Object _res) {
		ChannelHandlerContext channelContext = get(CHANNEL_CONTEXT);
		if (channelContext != null) {
			if (_res instanceof byte[]) {
				byte[] bytes = (byte[]) _res;

				ByteBuf buf = channelContext.alloc().buffer(bytes.length);
				buf.writeBytes(bytes);

				return channelContext.writeAndFlush(buf);
			} else {
				return channelContext.writeAndFlush(_res);
			}
		} else {
			throw new NullPointerException("Session");
		}
	}
}
