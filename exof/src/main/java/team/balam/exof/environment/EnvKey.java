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
		
		String INIT_LOG = "initLog";
		
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
	}
	
	interface Service
	{
		String SCHEDULE = "schedule";
		String SERVICE = "service";
	}
	
	interface Listener
	{
		String PORT = "port";
	}
}
