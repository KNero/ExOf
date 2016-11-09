package team.balam.exof.container.console;

import java.io.StringWriter;
import java.lang.reflect.Modifier;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConsoleCommandHandler extends SimpleChannelInboundHandler<String>
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private Gson gson;
	
	private ConsoleService consoleService;
	
	public ConsoleCommandHandler()
	{
		this.consoleService = new ConsoleService();
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
		
		Object response = null;
		
		switch(command.getType())
		{
			case Command.Type.SHOW_SERVICE_LIST :
				response = this.consoleService.getServiceList();
				break;
				
			case Command.Type.SHOW_SCHEDULE_LIST :
				response = this.consoleService.getScheduleList();

				break;
		}
		
		StringWriter writer = new StringWriter();
		this.objectMapper.writeValue(writer, response);
		
		ctx.writeAndFlush(writer.toString() + "\0");
	}
}
