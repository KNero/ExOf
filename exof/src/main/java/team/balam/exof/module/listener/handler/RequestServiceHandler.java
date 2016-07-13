package team.balam.exof.module.listener.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.listener.handler.transform.ServiceObjectTransform;
import team.balam.exof.module.service.Service;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceProvider;

@Sharable
public class RequestServiceHandler extends ChannelInboundHandlerAdapter
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("rawtypes")
	private ServiceObjectTransform transform;
	private SessionEventHandler sessionEventHandler;
	
	public void setServiceObjectTransform(ServiceObjectTransform<?> _transform)
	{
		this.transform = _transform;
	}
	
    public void setSessionEventHandler(SessionEventHandler sessionEventHandler)
	{
		this.sessionEventHandler = sessionEventHandler;
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) 
    {
    	try 
    	{
    		RequestContext.set(RequestContext.CHANNEL_CONTEXT, ctx);
    		
    		if(this.transform == null)
    		{
    			throw new Exception("messageTransform setting is empty.");
    		}
    		
    		@SuppressWarnings("unchecked")
			ServiceObject serviceObject = this.transform.transform(msg);
    		
    		if(serviceObject == null)
    		{
    			throw new Exception("serviceObject is null.");
    		}
    		
			String servicePath = serviceObject.getServicePath();
			Service service = ServiceProvider.lookup(servicePath);
			
			long start = System.currentTimeMillis();
			
			try
			{
				service.call(serviceObject);
			}
			catch(Exception e)
			{
				this.logger.error("An error occurred during service execution.", e);
			}
			
			if(this.logger.isInfoEnabled())
			{
				long end = System.currentTimeMillis();
				this.logger.info("Service[{}] is completed. Elapsed : {} ms", servicePath, end - start);
			}
		} 
    	catch(Exception e) 
    	{
    		this.logger.error("Message transform failed.", e);
		}
    	finally
    	{
    		RequestContext.remove();
    		ReferenceCountUtil.release(msg);
    	}
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) 
    {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
    {
        this.logger.error("An error has occurred in the handler.", cause);
        
        ctx.close();
        
        if(this.sessionEventHandler != null) this.sessionEventHandler.exceptionCaught(ctx, cause);
    }
    
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception
	{
		super.channelRegistered(ctx);
		
		 if(this.sessionEventHandler != null) this.sessionEventHandler.openedSession(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception
	{
		super.channelUnregistered(ctx);
		
		if(this.logger.isInfoEnabled())
		{
			this.logger.info("Session is closed. {}", ctx.channel().toString());
		}
		
		if(this.sessionEventHandler != null) this.sessionEventHandler.closedSession(ctx);
	}
}
