package team.balam.exof;

/**
 * 프레임워크를 구성하는 부분.<br/>
 * 새로운 컨테이너를 추가하여 완전히 독립적인 컨테이너을 추가할 수 있다.
 * @author kwonsm
 *
 */
public interface Container 
{
	String getName();

	default void init() {

	}

	void start() throws Exception;
	
	void stop() throws Exception;
}
