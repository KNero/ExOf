package team.balam.exof.module.service;

public interface Service
{
	String getServiceVariable(String _name);
	
	void call(ServiceObject _so) throws Exception;
}
