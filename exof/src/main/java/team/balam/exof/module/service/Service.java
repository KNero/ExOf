package team.balam.exof.module.service;

public interface Service
{
	String getVariable(String _name);
	
	void startup() throws Exception;
	
	void shutdown() throws Exception;
	
	void call(ServiceObject _so) throws Exception;
}
