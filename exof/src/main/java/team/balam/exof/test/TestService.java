package team.balam.exof.test;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.util.HttpResponseBuilder;

import java.util.List;

@ServiceDirectory
public class TestService {
	private Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Variable("schedule") public String scheduleA;
	@Variable("schedule") public String scheduleB;
	@Variable("schedule") public String scheduleC;

	@Variable("arrayParam") public String arrayParamA;
	@Variable("arrayParam") public String arrayParamB;
	@Variable("arrayParam") public List<String> arrayParamC;
	
	@Service
	@Inbound(TestInbound.class)
	@Outbound(TestOutbound.class)
	public String schedule() {
		this.logger.info("Service Variable : " + this.scheduleA + " / " + this.scheduleB + " / " + this.scheduleC);
		
		return "END";
	}

	@Service
	public void arrayParam() {
		this.logger.info("Service Variable : " + this.arrayParamA + " / " + this.arrayParamB + " / " + this.arrayParamC);
	}

	@Service
	@Inbound(TestInbound.class)
	@Outbound(TestOutbound.class)
	public void receive(Object _req) {
		this.logger.info("Receive data : " + _req.toString());

		RequestContext.writeAndFlushResponse("response\0".getBytes());
	}

	@Service
	public HttpResponse receiveHttp(FullHttpRequest _req) {
		this.logger.info("Receive http data : " + _req.toString());

		return HttpResponseBuilder.buildOkMessage("response");
	}
}
