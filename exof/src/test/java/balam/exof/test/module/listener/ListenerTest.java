package balam.exof.test.module.listener;

import org.junit.Test;
import org.mockito.runners.ConsoleSpammingMockitoJUnitRunner;
import team.balam.exof.Constant;
import team.balam.exof.db.ListenerDao;
import team.balam.exof.environment.ListenerLoader;
import team.balam.exof.module.listener.Listener;
import team.balam.util.sqlite.connection.DatabaseLoader;
import team.balam.util.sqlite.connection.pool.AlreadyExistsConnectionException;

import java.net.BindException;

public class ListenerTest {
	@Test
	public void test_load() throws Exception {
		ListenerDao.initTable();
		new ListenerLoader().load("./env");

		// 테스트 시 ClientTest 를 위해서 서버를 기동시켜 두기 때문에 BindException 이 발생한다
//		Listener.getInstance().start();
	}
}
