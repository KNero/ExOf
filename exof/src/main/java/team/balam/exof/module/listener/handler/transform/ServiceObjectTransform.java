package team.balam.exof.module.listener.handler.transform;

import team.balam.exof.module.service.ServiceObject;

public interface ServiceObjectTransform<T>
{
	ServiceObject transform(T _msg) throws Exception;
}
