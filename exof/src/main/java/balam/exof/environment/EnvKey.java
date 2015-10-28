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
		
		String AUTORELOAD_PARAM = "autoReloadParameter";
	}
	
	interface Service
	{
		String SCHEDULE = "schedule";
		String SERVICE = "service";
		
		String NAME = "name";
		String CLASS = "class";
		
		String CLON = "cronExpression";
		String DUPLICATE = "duplicateExecution";
		String PARAM_GROUP = "parameterGroup";
		String PARAM = "parameter";
	}
}
