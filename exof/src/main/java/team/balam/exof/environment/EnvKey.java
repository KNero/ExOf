package team.balam.exof.environment;

/**
 * 설정 파일의 키값을 저장.
 * @author kwonsm
 *
 */
public interface EnvKey 
{
	String HOME = "exof.envPath";
	
	interface PreFix
	{
		String FRAMEWORK = "framework.";
		String SERVICE = "service.";
		String LISTENER = "listener.";
	}
	
	interface Framework
	{
		String FRAMEWORK = "framework";
		String CONTAINER = "container";
		String SCHEDULER = "scheduler";
		
		String AUTORELOAD_SCHEDULER = "autoReload.scheduler";
		String AUTORELOAD_SERVICE_VARIABLE = "autoReload.serviceVariable";
		
		String JETTY_USE = "jetty.use";
		String JETTY_HTTP = "jetty.http";
		String JETTY_HTTPS = "jetty.https";
		String JETTY_MAX_IDLE = "jetty.maxIdleTime";
		String JETTY_HEADER_SIZE = "jetty.requestHeaderSize";
		String JETTY_DESCRIPTOR = "jetty.descriptor";
		String JETTY_RESOURCE_BASE = "jetty.resourceBase";
		String JETTY_CONTEXT_PATH = "jetty.contextPath";
		String JETTY_SSL_CONTEXT = "jetty.sslContextClass";
	}
	
	interface Service
	{
		String SCHEDULE = "schedule";
		String SERVICE = "service";
	}
	
	interface Listener
	{
		String LISTENER = "listener";
		String PORT = "port";
		String NUMBER = "number";
		String WORKER_SIZE = "workerSize";
		String MAX_LENGTH = "maxLength";
		
		String CHANNEL_HANDLER = "channelHandler";
		String MESSAGE_TRANSFORM = "messageTransform";
		String SESSION_HANDLER = "sessionHandler";
		
		String LENGTH_OFFSET = "lengthOffset";
		String LENGTH_SIZE = "lengthSize";
		
		String SSL = "ssl";
		String CERTIFICATE_PATH = "certificatePath";
		String PRIVATE_KEY_PATH = "privateKeyPath";
		
		String CONSOLE = "console";
	}
}
