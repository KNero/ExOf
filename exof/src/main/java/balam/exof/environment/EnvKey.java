package balam.exof.environment;

/**
 * 설정 파일의 키값을 저장.
 * @author kwonsm
 *
 */
public interface EnvKey 
{
	interface Framework
	{
		String FRAMEWORK = "framework";
		String CONTAINER = "container";
		String SCHEDULER = "scheduler";
	}
	
	interface Service
	{
		String SCHEDULE = "schedule";
		String SERVICE = "service";
		
		String CLASS = "class";
		String CLON = "cronExpression";
		String DUPLICATE = "duplicateExecution";
		String PARAM_GROUP = "parameterGroup";
		String PARAM = "parameter";
	}
}
