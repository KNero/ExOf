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
import team.balam.exof.module.service.component.http.HttpGet;
import team.balam.exof.module.service.component.http.HttpPost;
import team.balam.exof.module.service.component.http.JsonToMap;
import team.balam.exof.module.service.component.http.QueryStringToMap;
import team.balam.exof.util.HttpResponseBuilder;

import java.util.List;
import java.util.Map;

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

	@Service
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public HttpResponse receiveHttpGet(Map<String, Object> param) {
		this.logger.info("Receive http data : {}", param);

		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) param.get("list[]");
		if (!"권1".equals(list.get(0)) || !"권2".equals(list.get(1)) || !"권3".equals(list.get(2)) || !"권4".equals(list.get(3))) {
			return HttpResponseBuilder.buildServerError("wrong param");
		}

		if (!"pA".equals(param.get("paramA")) || !"pB".equals(param.get("paramB")) || !"권성민".equals(param.get("name"))) {
			return HttpResponseBuilder.buildServerError("wrong param");
		} else {
			return HttpResponseBuilder.buildOkMessage("response");
		}
	}

	@Service
	@Inbound({HttpPost.class, JsonToMap.class})
	public HttpResponse receiveHttpPost(Map<String, Object> param) {
		if ("aaaa".equals(param.get("a")) && "BBB".equals(param.get("b")) && new Integer(123).equals(param.get("number")) && "권성민".equals(param.get("name"))) {
			return HttpResponseBuilder.buildOkMessage("response");
		} else {
			return HttpResponseBuilder.buildServerError("wrong param");
		}
	}
}
