package team.balam.exof.module.listener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

import team.balam.exof.module.service.ServiceObject;

public class RequestContext 
{
	private static final String CHANNEL_CONTEXT = "c_c";
	private static final String SERVICE_OBJECT = "s_o";
	
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
	
	public static void setSession(ChannelHandlerContext _ctx)
	{
		set(CHANNEL_CONTEXT, _ctx);
	}
	
	public static ChannelHandlerContext getSession()
	{
		return get(CHANNEL_CONTEXT);
	}
	
	public static void setServiceObject(ServiceObject _object)
	{
		set(SERVICE_OBJECT, _object);
	}
	
	public static ServiceObject getServiceObject()
	{
		return get(SERVICE_OBJECT);
	}
	
	public static void remove()
	{
		context.remove();
	}
	
	public static void writeResponse(byte[] _msg)
	{
		ChannelHandlerContext channelContext = getSession();
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
		ChannelHandlerContext channelContext = getSession();
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
		ChannelHandlerContext channelContext = getSession();
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
