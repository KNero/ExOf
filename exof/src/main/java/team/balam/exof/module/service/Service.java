package team.balam.exof.module.service;

import java.util.Set;

public interface Service
{
	/**
	 * Can get the object of serviceDirectory set class. (service.xml)<br>
	 * All service is same in service directory.
	 * @return The object of serviceDirectory set class
	 */
	<T> T getHost();
	
	Object getServiceVariable(String _name);
	
	Set<String> getServiceVariableKeys();
	
	void call(ServiceObject _so) throws Exception;
}
