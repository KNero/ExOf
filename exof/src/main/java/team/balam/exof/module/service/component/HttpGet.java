package team.balam.exof.module.service.component;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.util.HttpResponseBuilder;

public class HttpGet implements Inbound {
	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException {
		HttpRequest request = (HttpRequest) _se.getRequest();
		if (!HttpMethod.GET.equals(request.method())) {
			FullHttpResponse response = HttpResponseBuilder.buildNotImplemented("Http method is must GET");
			RequestContext.writeAndFlushResponse(response);

			throw new InboundExecuteException("Bad http method. HTTP method is must GET. " + _se.getServicePath());
		}
	}
}
