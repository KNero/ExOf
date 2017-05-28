package team.balam.exof.container.console;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Function;

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
	private Function<Command, Boolean> filter;
	
	public ConsoleCommandHandler() {
		this.consoleService = new ConsoleService();
		this.gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC)
				.excludeFieldsWithoutExposeAnnotation().create();
	}
	
	public void setFilter(Function<Command, Boolean> filter) {
		this.filter = filter;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		String responseJson = this.executeConsoleService(msg);
		
		if (responseJson != null) {
			ctx.writeAndFlush(responseJson + "\0");
		}
	}
	
	public String executeConsoleService(String _json) throws Exception {
		Command command = this.gson.fromJson(_json, Command.class);
		
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Command type : {}", command.getType());
		}
		
		Object response = null;
		
		if (this.filter == null || this.filter.apply(command)) {
			Method service = ConsoleService.class.getMethod(command.getType(), Map.class);
			response = service.invoke(this.consoleService, command.getParameter());
		} else {
			response = Command.NO_DATA_RESPONSE;
		}
		
		if (response instanceof String) {
			return (String)response;	
		} else {
			StringWriter writer = new StringWriter();
			this.objectMapper.writeValue(writer, response);
			
			return writer.toString();
		}
	}
}
