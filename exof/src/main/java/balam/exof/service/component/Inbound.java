package balam.exof.service.component;

import balam.exof.service.ServiceObject;

public interface Inbound
{
	void execute(ServiceObject _se) throws InboundExecuteException;
}
