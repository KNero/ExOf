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

	/**
	 * 멤버 변수로 사용할 경우 직접 호출 시 사용하는 것으로 예외를 전달 받을 수 있다.
	 * @param parameter 서비스로 등록된 메소드의 parameter
	 */
	<T> T call(Object... parameter) throws Exception;
}
