package team.balam.exof;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import team.balam.exof.Constant;
import team.balam.util.sqlite.connection.DatabaseLoader;

import java.io.File;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InitTest {
	public static final String TEST_DB = "./target/" + Constant.ENV_DB;
	@Test
	public void test01_init() throws Exception {
		File dbFile = new File(TEST_DB);
		dbFile.deleteOnExit();

		DatabaseLoader.load(Constant.ENV_DB, TEST_DB);
	}
}
