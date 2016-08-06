package team.balam.exof.container.console;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.service.ServiceProvider;

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
		
		ctx.writeAndFlush(responseJson + "\0");
	}
	
	private String _getServiceList() throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		
		Map<String, HashMap<String, String>> result = ServiceProvider.getInstance().getAllServiceInfo();
		if(result.size() == 0)
		{
			return Command.NO_DATA_RESPONSE;
		}
		else
		{
			StringWriter writer = new StringWriter();
			objectMapper.writeValue(writer, result);
			
			return writer.toString();
		}
	}
}
