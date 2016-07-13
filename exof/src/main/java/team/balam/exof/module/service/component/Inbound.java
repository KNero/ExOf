package team.balam.exof.module.service.component;

import team.balam.exof.module.service.ServiceObject;

public interface Inbound
{
	void execute(ServiceObject _se) throws InboundExecuteException;
}
