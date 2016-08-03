package team.balam.exof.container.console;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.listener.handler.transform.JsonTransform;
import team.balam.exof.module.service.ServiceObject;

public class CommanHandler extends SimpleChannelInboundHandler<String>
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private JsonTransform jsonTransformer = new JsonTransform();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
	{
		ServiceObject serviceObject = this.jsonTransformer.transform(msg);
		
		@SuppressWarnings("unchecked")
		HashMap<String, Object> request = (HashMap<String, Object>)serviceObject.getRequest();
		
		this.logger.error(request.toString());
	}
}
