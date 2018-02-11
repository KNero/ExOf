package team.balam.exof;

import team.balam.exof.db.DynamicSettingDao;
import team.balam.exof.db.ListenerDao;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.FrameworkLoader;
import team.balam.exof.environment.ServiceLoader;
import team.balam.exof.module.service.ServiceProvider;
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

			DatabaseLoader.load(Constant.ENV_DB, TEST_DB);
			ServiceInfoDao.initTable();
			ListenerDao.initTable();
			DynamicSettingDao.createTable();

			ExternalClassLoader.load("./lib/external");

			new ServiceLoader().load("./env");
			new FrameworkLoader().load("./env");

			ServiceProvider.getInstance().start();
		}
	}
}
