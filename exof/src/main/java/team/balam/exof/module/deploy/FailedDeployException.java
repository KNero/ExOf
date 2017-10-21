package team.balam.exof.module.deploy;

public class FailedDeployException extends Exception {
	FailedDeployException(String _msg) {
		super(_msg);
	}

	FailedDeployException(String _msg, Exception e) {
		super(_msg, e);
	}

	FailedDeployException(Exception e) {
		super(e);
	}
}
