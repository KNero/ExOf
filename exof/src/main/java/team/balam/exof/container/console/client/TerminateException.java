package team.balam.exof.container.console.client;

import java.io.IOException;

public class TerminateException extends IOException {
	private static final long serialVersionUID = 1L;

	public TerminateException() {

	}

	public TerminateException(String _message) {
		super(_message);
	}
}
