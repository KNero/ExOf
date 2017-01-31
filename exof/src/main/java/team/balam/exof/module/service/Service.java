package team.balam.exof.module.service;

import java.util.Map;

public interface Service
{
	String getServiceVariable(String _name);
	
	Map<String, String> getAllServiceVariable();
	
	void call(ServiceObject _so) throws Exception;
}
