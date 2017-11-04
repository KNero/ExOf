package team.balam.exof.environment;

import team.balam.exof.Constant;
import team.balam.exof.db.DynamicSettingDao;
import team.balam.exof.db.ListenerDao;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.util.sqlite.connection.DatabaseLoader;

public class EnvDbLoader implements Loader {
	@Override
	public void load(String _envPath) throws LoadEnvException {
		try {
			DatabaseLoader.load(Constant.ENV_DB, _envPath + "/" + Constant.ENV_DB);

			DynamicSettingDao.createTable();
			ServiceInfoDao.initTable();
			ListenerDao.initTable();
		} catch (Exception e) {
			throw new LoadEnvException("Can not create env db table", e);
		}
	}
}
