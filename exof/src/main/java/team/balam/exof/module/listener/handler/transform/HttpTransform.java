package team.balam.exof.listener.handler.transform;

import io.netty.handler.codec.http.FullHttpRequest;
import team.balam.exof.service.ServiceObject;

public class HttpTransform implements ServiceObjectTransform<FullHttpRequest>
{
	@Override
	public ServiceObject transform(FullHttpRequest _msg) throws Exception 
	{
		FullHttpRequest httpRequest = (FullHttpRequest)_msg;
		
		ServiceObject serviceObject = new ServiceObject(httpRequest.getUri());
		serviceObject.setRequest(httpRequest);

		return serviceObject;
	}
}
