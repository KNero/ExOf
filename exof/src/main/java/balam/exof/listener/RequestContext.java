package balam.exof.listener;

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
	
	public static void set(String _key, Object _value)
	{
		HashMap<String, Object> c =  context.get();
		
		if(c == null) c = _createDefaultContext();
		
		c.put(_key, _value);
	}
	
	public static Object get(String _key)
	{
		HashMap<String, Object> c =  context.get();
		
		if(c == null) c = _createDefaultContext();
		
		return c.get(_key);
	}
	
	public static void remove()
	{
		context.remove();
	}
	
	public static void writeResponse(byte[] _msg)
	{
		ChannelHandlerContext channelContext = (ChannelHandlerContext)get(CHANNEL_CONTEXT);
		if(channelContext != null)
		{
			ByteBuf buf = channelContext.alloc().buffer(_msg.length);
			buf.writeBytes(_msg);
			
			channelContext.write(buf);
			channelContext.flush();
		}
		else
		{
			throw new NullPointerException("Session");
		}
	}
}
