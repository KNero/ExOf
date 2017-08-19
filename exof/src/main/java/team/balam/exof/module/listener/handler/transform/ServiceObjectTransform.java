package team.balam.exof.module.listener.handler.transform;

import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.service.ServiceObject;

public interface ServiceObjectTransform<T>
{
	default void init(PortInfo _portInfo)
	{
		
	}
	
	ServiceObject transform(T _msg) throws Exception;
}
