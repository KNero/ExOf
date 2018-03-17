package team.balam.exof.module.service;

public interface ServiceWrapper
{
	/**
	 * is internal service.
	 * @return Service annotation's internal attribute value.
	 */
	boolean isInternal();

	/**
	 * Can get the object of serviceDirectory set class. (service.xml)<br>
	 * All service have same object in service directory.
	 * @return The object of serviceDirectory set class
	 */
	<T> T getHost();

	String getMethodName();

	<T> T call(ServiceObject so) throws Exception;
}
