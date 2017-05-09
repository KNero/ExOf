package team.balam.exof.container.console;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConsoleCommandHandler extends SimpleChannelInboundHandler<String> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private Gson gson;
	
	private ConsoleService consoleService;
	
	public ConsoleCommandHandler() {
		this.consoleService = new ConsoleService();
		this.gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC)
				.excludeFieldsWithoutExposeAnnotation().create();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		String responseJson = this.executeConsoleService(msg);
		
		ctx.writeAndFlush(responseJson + "\0");
	}
	
	public String executeConsoleService(String _json) throws Exception {
		Command command = this.gson.fromJson(_json, Command.class);
		
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Command type : {}", command.getType());
		}
		
		Method service = ConsoleService.class.getMethod(command.getType(), Map.class);
		Object response = service.invoke(this.consoleService, command.getParameter());
		
		if (response instanceof Map) {
			StringWriter writer = new StringWriter();
			this.objectMapper.writeValue(writer, response);
			
			return writer.toString();
		} else {
			return (String)response;			
		}
	}
}
