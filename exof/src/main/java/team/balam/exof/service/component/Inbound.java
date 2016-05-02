package team.balam.exof.service.component;

import team.balam.exof.service.ServiceObject;

public interface Inbound
{
	void execute(ServiceObject _se) throws InboundExecuteException;
}
