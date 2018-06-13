package team.balam.exof.module.listener.handler.transform;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import team.balam.exof.module.service.ServiceObject;

public class HttpTransform implements ServiceObjectTransform<FullHttpRequest>
{

	@Override
	public ServiceObject transform(FullHttpRequest request)
	{
		QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
		ServiceObject serviceObject = new ServiceObject(decoder.path());
		serviceObject.setRequest(request);
		serviceObject.setServiceGroupId(request.method().name());

		return serviceObject;
	}
}
