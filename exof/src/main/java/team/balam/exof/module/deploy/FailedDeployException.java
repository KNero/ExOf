package team.balam.exof.module.deploy;

public class FailedDeployException extends Exception {
	FailedDeployException(String _msg) {
		super(_msg);
	}

	FailedDeployException(Exception e) {
		super(e);
	}
}
