package balam.exof;

/**
 * Framework를 구성하는 요소
 * @author kwonsm
 *
 */
public interface Module
{
	void start() throws Exception;
	
	void stop() throws Exception;
}
