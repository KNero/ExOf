package balam.exof.service.component;

public interface Outbound
{
	void execute(Object _result) throws OutboundExecuteException;
}
