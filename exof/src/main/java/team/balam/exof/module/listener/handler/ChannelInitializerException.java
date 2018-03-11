package team.balam.exof.module.listener.handler;

public class ChannelInitializerException extends Exception {
	public ChannelInitializerException(String message) {
		super(message);
	}

	public ChannelInitializerException(String message, Exception e) {
		super(message, e);
	}
}
