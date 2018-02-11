package team.balam.exof;

import team.balam.exof.db.DynamicSettingDao;
import team.balam.exof.db.ListenerDao;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.util.sqlite.connection.DatabaseLoadException;
import team.balam.util.sqlite.connection.DatabaseLoader;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestInitializer {
	private static final String TEST_DB = "./target/" + Constant.ENV_DB;
	private static final AtomicBoolean IS_INIT = new AtomicBoolean();

	public static void init() throws Exception {
		if (IS_INIT.compareAndSet(false, true)) {
			File dbFile = new File(TEST_DB);
			dbFile.deleteOnExit();

			try {
				DatabaseLoader.load(Constant.ENV_DB, TEST_DB);
			} catch (DatabaseLoadException e) {
				//ignore
			}
			ServiceInfoDao.initTable();
			ListenerDao.initTable();
			DynamicSettingDao.createTable();

			ExternalClassLoader.load("./lib/external");
		}
	}
}
