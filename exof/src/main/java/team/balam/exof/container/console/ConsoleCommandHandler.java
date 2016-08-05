package team.balam.exof.container.console;

import java.lang.reflect.Modifier;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConsoleCommandHandler extends SimpleChannelInboundHandler<String>
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Gson gson;
	
	public ConsoleCommandHandler()
	{
		this.gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC)
				.excludeFieldsWithoutExposeAnnotation().create();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
	{
		Command command = this.gson.fromJson(msg, Command.class);
		
		if(this.logger.isInfoEnabled())
		{
			this.logger.info("Command type : {}", command.getType());
		}
		
		String responseJson = null;
		
		switch(command.getType())
		{
			case Command.Type.SHOW_SERVICE_LIST :
				responseJson = this._getServiceList();
				break;
		}
		
		ctx.writeAndFlush(responseJson);
	}
	
	private String _getServiceList()
	{
		return "{\"test\":\"test\"}\0";
	}
}
