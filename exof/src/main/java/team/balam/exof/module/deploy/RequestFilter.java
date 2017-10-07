package team.balam.exof.module.deploy;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;
import team.balam.exof.util.HttpResponseBuilder;

public class RequestFilter implements Inbound{
	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException {
		FullHttpRequest request = (FullHttpRequest) _se.getRequest();
		if (!HttpMethod.POST.equals(request.method())) {
			FullHttpResponse response = HttpResponseBuilder.buildNotImplemented("Deploy is must POST.");
			RequestContext.writeAndFlushResponse(response);

			throw new InboundExecuteException("Bad http method. Deploy method is must POST." + _se);
		}
	}
}
