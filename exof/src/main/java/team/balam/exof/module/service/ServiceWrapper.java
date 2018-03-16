package team.balam.exof.module.service;

public interface ServiceWrapper
{
	/**
	 * Can get the object of serviceDirectory set class. (service.xml)<br>
	 * All service is same in service directory.
	 * @return The object of serviceDirectory set class
	 */
	<T> T getHost();

	String getMethodName();

	<T> T call(ServiceObject so) throws Exception;
}
