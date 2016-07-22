package team.balam.exof.module.listener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

public class RequestContext 
{
	public static final String CHANNEL_CONTEXT = "c_c";
	
	private static ThreadLocal<HashMap<String, Object>> context = new ThreadLocal<HashMap<String, Object>>();
	
	private static HashMap<String, Object> _createDefaultContext()
	{
		HashMap<String, Object> c = new HashMap<String, Object>();
		context.set(c);
		
		return c;
	}
	
	public static void set(String _key, Object _obj)
	{
		HashMap<String, Object> c =  context.get();
		
		if(c == null)
		{
			c = _createDefaultContext();
		}
		
		c.put(_key, _obj);
	
	}
	
	public static void setSession(ChannelHandlerContext _ctx)
	{

		HashMap<String, Object> c =  context.get();
		
		if(c == null)
		{
			c = _createDefaultContext();
		}
		
		c.put(CHANNEL_CONTEXT, _ctx);
	
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String _key)
	{
		HashMap<String, Object> c =  context.get();
		
		if(c == null)
		{
			c = _createDefaultContext();
		}
		
		return (T)c.get(_key);
	}
	
	public static ChannelHandlerContext getSession()
	{
		HashMap<String, Object> c =  context.get();
		
		if(c == null)
		{
			c = _createDefaultContext();
		}
		
		return (ChannelHandlerContext)c.get(CHANNEL_CONTEXT);
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
	
	public static void writeResponse(Object _res)
	{
		ChannelHandlerContext channelContext = getSession();
		if(channelContext != null)
		{
			channelContext.writeAndFlush(_res);
		}
		else
		{
			throw new NullPointerException("Session");
		}
	}
}
