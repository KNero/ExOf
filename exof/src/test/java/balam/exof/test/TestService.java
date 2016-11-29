package balam.exof.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;

@ServiceDirectory
public class TestService
{
	private Logger logger  = LoggerFactory.getLogger(this.getClass());
	
	@Service
	public void receiveByte(byte[] _req)
	{
		this.logger.info("Receive data : " + new String(_req));
		
		byte[] res = "response".getBytes();
		
		ByteBuf buf = Unpooled.buffer();
		buf.writeInt(res.length);
		buf.writeBytes(res);
		
		RequestContext.writeAndFlushResponse(buf);
	}
}
