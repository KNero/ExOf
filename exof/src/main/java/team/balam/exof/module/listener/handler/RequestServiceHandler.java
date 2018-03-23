package team.balam.exof.module.listener.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.listener.handler.transform.BadFormatException;
import team.balam.exof.module.listener.handler.transform.ServiceObjectTransform;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.ServiceWrapper;

@Sharable
public class RequestServiceHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(RequestServiceHandler.class);

	private ServiceObjectTransform transform;
	private SessionEventHandler sessionEventHandler;
	
	public void setServiceObjectTransform(ServiceObjectTransform<?> transform)
	{
		this.transform = transform;
	}
	
    public void setSessionEventHandler(SessionEventHandler sessionEventHandler)
	{
		this.sessionEventHandler = sessionEventHandler;
	}

    @SuppressWarnings("unchecked")
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	ServiceObject serviceObject = null;

    	try {
    		serviceObject = this.transform.transform(msg);
    		if(serviceObject == null) {
    			throw new NullPointerException("serviceObject is null.");
    		}
    		
    		RequestContext.set(RequestContext.Key.SERVICE_OBJECT, serviceObject);
		    RequestContext.set(RequestContext.Key.CHANNEL_CONTEXT, ctx);
		    RequestContext.set(RequestContext.Key.ORIGINAL_REQUEST, msg);

			callService(serviceObject);
		} catch(BadFormatException bad) {
    		LOG.error("Session is closed. Because message is bad format.", bad);
    		ctx.close();
    	} catch(Exception e) {
    		LOG.error("Can not execute service.", e);
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

    private static void callService(ServiceObject serviceObject) throws Exception {
	    String servicePath = serviceObject.getServicePath();
	    ServiceWrapper service = ServiceProvider.lookup(servicePath);
	    if (service.isInternal()) {
			throw new CallInternalServiceException(serviceObject.getServicePath());
	    }

	    long start = System.currentTimeMillis();
	    Object response = service.call(serviceObject);

	    if(LOG.isInfoEnabled()) {
		    long end = System.currentTimeMillis();
		    LOG.info("Service[{}] is completed. Elapsed : {} ms", serviceObject.getServicePath(), end - start);
	    }

	    if (response != null) {
		    RequestContext.writeAndFlushResponse(response);
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
        LOG.error("An error has occurred in the handler.", cause);
        
        ctx.close();
        
        if(this.sessionEventHandler != null) this.sessionEventHandler.exceptionCaught(ctx, cause);
    }
    
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception
	{
		super.channelRegistered(ctx);
		
		if(this.sessionEventHandler != null) {
			this.sessionEventHandler.openedSession(ctx);
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		
		if(LOG.isInfoEnabled()) {
			LOG.info("Session is closed. {}", ctx.channel().toString());
		}
		
		if(this.sessionEventHandler != null) {
			this.sessionEventHandler.closedSession(ctx);
		}
	}
}
