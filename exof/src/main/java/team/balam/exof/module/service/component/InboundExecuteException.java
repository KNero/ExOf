package team.balam.exof.module.service.component;

public class InboundExecuteException extends Exception {
	private static final long serialVersionUID = -8935499671612950840L;
	
	public InboundExecuteException(String _msg, Exception _e) {
		super(_e);
	}
	
	public InboundExecuteException(String _msg) {
		super(_msg);
	}
}
