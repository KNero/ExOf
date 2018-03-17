package team.balam.exof.module.listener.handler;

public class CallInternalServiceException extends Exception {
	CallInternalServiceException(String servicePath) {
		super("Can not call internal service. service path:" + servicePath);
	}
}
