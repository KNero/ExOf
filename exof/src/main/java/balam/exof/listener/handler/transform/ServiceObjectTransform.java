package balam.exof.listener.handler.transform;

import balam.exof.service.ServiceObject;

public interface ServiceObjectTransform<T>
{
	ServiceObject transform(T _msg) throws Exception;
}
