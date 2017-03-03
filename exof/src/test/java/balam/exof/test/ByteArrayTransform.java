package balam.exof.test;

import team.balam.exof.module.listener.handler.transform.ServiceObjectTransform;
import team.balam.exof.module.service.ServiceObject;

public class ByteArrayTransform implements ServiceObjectTransform<byte[]>
{
	@Override
	public ServiceObject transform(byte[] _msg) throws Exception
	{
		ServiceObject so = new ServiceObject("/test/receiveByte");
		so.setRequest(_msg);
		
		return so;
	}
}
