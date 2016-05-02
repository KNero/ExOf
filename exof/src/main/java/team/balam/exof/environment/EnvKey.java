package team.balam.exof.environment;

/**
 * 설정 파일의 키값을 저장.
 * @author kwonsm
 *
 */
public interface EnvKey 
{
	String HOME = "envPath";
	
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
		
		String AUTORELOAD = "autoReload";
		String AUTORELOAD_SCHEDULER = "autoReload.scheduler";
		String AUTORELOAD_SERVICE_VARIABLE = "autoReload.serviceVariable";
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
