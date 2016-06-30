package team.balam.exof.listener.handler.transform;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import team.balam.exof.service.ServiceObject;

public class HttpTransform implements ServiceObjectTransform<Object>
{
	@Override
	public ServiceObject transform(Object _msg) throws Exception 
	{
		if(_msg instanceof HttpRequest)
		{
			HttpRequest httpRequest = (HttpRequest)_msg;
		}
		
		if(_msg instanceof HttpContent)
		{
			HttpContent httpContent = (HttpContent)_msg;
			
			if(_msg instanceof LastHttpContent)
			{
				
			}
		}

		return null;
	}
}
