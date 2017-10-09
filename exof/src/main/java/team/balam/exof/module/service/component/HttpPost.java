package team.balam.exof.module.service.component;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.util.HttpResponseBuilder;

public class HttpPost implements Inbound {
	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException {
		HttpRequest request = (HttpRequest) _se.getRequest();
		if (!HttpMethod.POST.equals(request.method())) {
			FullHttpResponse response = HttpResponseBuilder.buildNotImplemented("Http method is must POST");
			RequestContext.writeAndFlushResponse(response);

			throw new InboundExecuteException("Bad http method. HTTP method is must POST. " + _se.getServicePath());
		}
	}
}
