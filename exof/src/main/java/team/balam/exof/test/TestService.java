package team.balam.exof.test;

import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.*;
import team.balam.exof.module.service.component.http.*;
import team.balam.exof.util.HttpResponseBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

@ServiceDirectory
public class TestService {
	private Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Variable public String scheduleA;
	@Variable public String scheduleB;
	@Variable public String scheduleC;

	@Variable public String arrayParamA;
	@Variable public String arrayParamB;
	@Variable public List<String> arrayParamC;
	
	@Service("schedule")
	@Inbound(TestInbound.class)
	@Outbound(TestOutbound.class)
	public String schedule() {
		this.logger.info("Service Variable : {}/{}/{}", this.scheduleA, this.scheduleB, this.scheduleC);
		
		return "END";
	}

	@Service("arrayParam")
	public void arrayParam() {
		this.logger.info("Service Variable : {}/{}/{}", this.arrayParamA, this.arrayParamB, this.arrayParamC);
	}

	@Service("receive")
	@Inbound(TestInbound.class)
	@Outbound(TestOutbound.class)
	public void receive(Object req) {
		this.logger.info("Receive data : {}", req);

		RequestContext.writeAndFlushResponse("response\0".getBytes());
	}

	@Service("receiveHttp")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public HttpResponse receiveHttp(Map<String, Object> request) {
		this.logger.info("response : {}", request.get("message"));

		return HttpResponseBuilder.buildOkMessage(request.get("message").toString());
	}

	@Service("http-get")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public HttpResponse receiveHttpGet(Map<String, Object> param) throws Exception {
		this.logger.info("Receive http data : {}", param);

		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) param.get("list[]");
		if (!"권1".equals(list.get(0)) || !"권2".equals(list.get(1)) || !"권3".equals(list.get(2)) || !"권4".equals(list.get(3))) {
			return HttpResponseBuilder.buildServerError("wrong param");
		}

		if (!"pA".equals(param.get("paramA")) || !"pB".equals(param.get("paramB")) || !"권성민".equals(param.get("name"))) {
			return HttpResponseBuilder.buildServerError("wrong param");
		}

		return HttpResponseBuilder.buildOkMessage("response");
	}

	@Service("http-get2")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public void receiveHttpGet2(@Variable("list[]") List<String> list, @Variable("paramA") String paramA, @Variable("paramB") String paramB,
	                            @Variable("name") String name) throws Exception {
		if (!"권1".equals(list.get(0)) || !"권2".equals(list.get(1)) || !"권3".equals(list.get(2)) || !"권4".equals(list.get(3))) {
			throw new Exception("receiveHttpGet - 1");
		}

		if (!"pA".equals(paramA) || !"pB".equals(paramB) || !"권성민".equals(name)) {
			throw new Exception("receiveHttpGet - 2");
		}
	}

	@Service("http-post")
	@Inbound({HttpPost.class, BodyToMap.class})
	public HttpResponse receiveHttpPost(Map<String, Object> param) {
		if ("aaaa".equals(param.get("a")) && "BBB".equals(param.get("b")) && new Integer(123).equals(param.get("number")) && "권성민".equals(param.get("name"))) {
			return HttpResponseBuilder.buildOkMessage("response");
		} else {
			return HttpResponseBuilder.buildServerError("wrong param");
		}
	}

	@Service("receiveHttpGet4Jetty")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public void receiveHttpGet4Jetty(Map<String, Object> param) throws IOException {
		HttpServletResponse response = RequestContext.get(RequestContext.Key.HTTP_SERVLET_RES);

		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) param.get("list[]");
		if (!"권1".equals(list.get(0)) || !"권2".equals(list.get(1)) || !"권3".equals(list.get(2)) || !"권4".equals(list.get(3))) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		if (!"pA".equals(param.get("paramA")) || !"pB".equals(param.get("paramB")) || !"권성민".equals(param.get("name"))) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else {
			Writer writer = response.getWriter();
			writer.write("success");
			writer.flush();
		}
	}

	@Service("receiveHttpPost4Jetty")
	@Inbound({HttpPost.class, BodyToMap.class})
	public void receiveHttpPost4Jetty(Map<String, Object> param) throws IOException {
		HttpServletResponse response = RequestContext.get(RequestContext.Key.HTTP_SERVLET_RES);

		if ("aaaa".equals(param.get("a")) && "BBB".equals(param.get("b")) && new Integer(123).equals(param.get("number")) && "권성민".equals(param.get("name"))) {
			Writer writer = response.getWriter();
			writer.write("success");
			writer.flush();
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Service("throwException")
	public void throwException() throws TestException {
		throw new TestException();
	}

	@Service
	public void emptyNameTest() {}

	@Service("wild1/{1}")
	public void testWildcard1() {}

	@Service("wild2/{2}")
	public void testWildcard2() {}

	@Service("wild1/{1}/1")
	public void testWildcard1_1() {}

	@Service("wild2/{2}/2")
	public void testWildcard2_2() {}

	@RestService(name = "/receive-file", method = HttpMethod.POST)
    public void receiveFile(HttpServletRequest request) {
        logger.info("request : {}", request);

        File file = (File) request.getAttribute("file");
        String fileName = request.getParameter("file");
        String hidden = request.getParameter("hidden");

        logger.info("hidden value: {}", hidden);
        logger.info("file name: {}, size: {}", fileName, file.length());
    }
}
