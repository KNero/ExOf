package balam.exof.environment;

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
	}
	
	interface Framework
	{
		String FRAMEWORK = "framework";
		String CONTAINER = "container";
		String SCHEDULER = "scheduler";
		
		String AUTORELOAD = "autoReload";
		String AUTORELOAD_SCHEDULER = "autoReload.scheduler";
	}
	
	interface Service
	{
		String SCHEDULE = "schedule";
		String SERVICE = "service";
	}
}
