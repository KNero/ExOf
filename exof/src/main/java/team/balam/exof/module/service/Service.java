package team.balam.exof.module.service;

import java.util.Set;

public interface Service
{
	/**
	 * Can get the object of serivceDirectory set class. (service.xml)<br>
	 * All service is same in service directory.
	 * @return The object of serivceDirectory set class
	 */
	<T> T getHost();
	
	Object getServiceVariable(String _name);
	
	Set<String> getServiceVariableKeys();
	
	void call(ServiceObject _so) throws Exception;
}
