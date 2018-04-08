package team.balam.exof.module.service.component;

public interface Outbound<I, O>
{
	O execute(I result) throws OutboundExecuteException;
}
