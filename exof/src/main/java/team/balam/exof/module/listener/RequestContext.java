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
	
	private static ThreadLocal<HashMap<String, Object>> context = new ThreadLocal<HashMap<String, Object>>();
	
	public static void createContext()
	{
		context.set(new HashMap<>());
	}
	
	public static void set(String _key, Object _obj)
	{
		HashMap<String, Object> c =  context.get();
		c.put(_key, _obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String _key)
	{
		HashMap<String, Object> c =  context.get();
		return (T)c.get(_key);
	}
	
	public static void remove()
	{
		context.remove();
	}
	
	public static void writeResponse(byte[] _msg)
	{
		ChannelHandlerContext channelContext = get(CHANNEL_CONTEXT);
		if(channelContext != null)
		{
			ByteBuf buf = channelContext.alloc().buffer(_msg.length);
			buf.writeBytes(_msg);
			
			channelContext.writeAndFlush(buf);
		}
		else
		{
			throw new NullPointerException("Session");
		}
	}
	
	public static ChannelFuture writeResponse(Object _res)
	{
		ChannelHandlerContext channelContext = get(CHANNEL_CONTEXT);
		if(channelContext != null)
		{
			return channelContext.write(_res);
		}
		else
		{
			throw new NullPointerException("Session");
		}
	}

	public static ChannelFuture writeAndFlushResponse(Object _res)
	{
		ChannelHandlerContext channelContext = get(CHANNEL_CONTEXT);
		if(channelContext != null)
		{
			return channelContext.writeAndFlush(_res);
		}
		else
		{
			throw new NullPointerException("Session");
		}
	}
}
