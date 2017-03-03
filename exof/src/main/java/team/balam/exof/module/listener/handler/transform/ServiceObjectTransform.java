package team.balam.exof.module.listener.handler.transform;

import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.service.ServiceObject;

public interface ServiceObjectTransform<T>
{
	void init(PortInfo _portInfo) throws Exception;
	
	ServiceObject transform(T _msg) throws Exception;
}
