package team.balam.exof.module.listener.handler.transform;

import io.netty.handler.codec.http.FullHttpRequest;
import team.balam.exof.module.service.ServiceObject;

public class HttpTransform implements ServiceObjectTransform<FullHttpRequest>
{
	@Override
	public ServiceObject transform(FullHttpRequest _msg) throws Exception 
	{
		ServiceObject serviceObject = new ServiceObject(_msg.uri());
		serviceObject.setRequest(_msg);

		return serviceObject;
	}
}
