package team.balam.exof.module.listener.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Request;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.listener.handler.transform.BadFormatException;
import team.balam.exof.module.listener.handler.transform.ServiceObjectTransform;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceProvider;

@Sharable
public class RequestServiceHandler extends ChannelInboundHandlerAdapter
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @SuppressWarnings("unchecked")
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
	    RequestContext.set(RequestContext.Key.CHANNEL_CONTEXT, ctx);
	    RequestContext.set(RequestContext.Key.ORIGINAL_REQUEST, msg);

    	ServiceObject serviceObject = null;
    	try {
    		serviceObject = this.transform.transform(msg);
    		if(serviceObject == null) {
    			throw new NullPointerException("serviceObject is null.");
    		}
    		
    		RequestContext.set(RequestContext.Key.SERVICE_OBJECT, serviceObject);

    		String servicePath = serviceObject.getServicePath();
			ServiceWrapper service = ServiceProvider.lookup(servicePath);

		    long start = System.currentTimeMillis();

		    Object response = service.call(serviceObject);
		    if (response != null) {
			    RequestContext.writeAndFlushResponse(response);
		    }

		    if(this.logger.isInfoEnabled()) {
			    long end = System.currentTimeMillis();
			    this.logger.info("Service[{}] is completed. Elapsed : {} ms", servicePath, end - start);
		    }
		} catch(BadFormatException bad) {
    		this.logger.error("Session is closed. Because message is bad format.", bad);
    		ctx.close();
    	} catch(Exception e) {
    		this.logger.error("Can not execute service.", e);

    		if (serviceObject != null && !serviceObject.isAutoCloseSession() && serviceObject.isCloseSessionByError()) {
			    ctx.close();
		    }
		} finally {
    		ReferenceCountUtil.release(msg);
    		
    		if(serviceObject != null && serviceObject.isAutoCloseSession()) {
    			ctx.close();
    		}
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
