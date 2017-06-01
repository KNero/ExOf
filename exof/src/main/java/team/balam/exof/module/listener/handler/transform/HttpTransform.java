package team.balam.exof.module.listener.handler.transform;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import team.balam.exof.module.service.ServiceObject;

public class HttpTransform implements ServiceObjectTransform<FullHttpRequest>
{

	@Override
	public ServiceObject transform(FullHttpRequest _msg) throws Exception 
	{
		QueryStringDecoder decoder = new QueryStringDecoder(_msg.uri());
		ServiceObject serviceObject = new ServiceObject(decoder.path());
		serviceObject.setRequest(_msg);

		return serviceObject;
	}
}
