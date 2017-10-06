package balam.exof.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import team.balam.exof.Constant;
import team.balam.exof.db.DynamicSettingDao;
import team.balam.exof.environment.DynamicSetting;
import team.balam.exof.environment.ServiceLoader;
import team.balam.exof.environment.vo.DynamicSettingVo;
import team.balam.util.sqlite.connection.DatabaseLoader;

import java.io.File;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InitTest {
	public static final String TEST_DB = "./target/" + Constant.ENV_DB;
	@Test
	public void test01_init() throws Exception {
		File dbFile = new File(TEST_DB);
		dbFile.delete();

		DatabaseLoader.load(Constant.ENV_DB, TEST_DB);
	}

	@Test
	public void test02_putDynamicSettingData() throws Exception {
		DynamicSettingDao.createTable();

		for (int i = 0; i < 10; ++i) {
			if (i == 5) {
				DynamicSetting.getInstance().put(new DynamicSettingVo("name" + i, "value" + i, null));
			} else {
				DynamicSetting.getInstance().put(new DynamicSettingVo("name" + i, "value" + i, "des" + i));
			}
		}
	}

	@Test
	public void test03_loadLoaderData() throws Exception {
		new ServiceLoader().load("./env");
	}
}
