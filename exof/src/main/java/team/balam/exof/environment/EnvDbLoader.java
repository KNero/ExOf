package team.balam.exof.environment;

import team.balam.exof.Constant;
import team.balam.exof.db.DynamicSettingDao;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.util.sqlite.connection.DatabaseLoader;

class EnvDbLoader implements Loader {
	@Override
	public void load(String _envPath) throws LoadEnvException {
		try {
			DatabaseLoader.load(Constant.ENV_DB, Constant.ENV_DB);

			DynamicSettingDao.createTable();
			ServiceInfoDao.createTable();
		} catch (Exception e) {
			throw new LoadEnvException("Can not create env db file", e);
		}
	}
}
