package team.balam.exof.db;

import team.balam.exof.Constant;
import team.balam.exof.container.scheduler.SchedulerInfo;
import team.balam.exof.environment.LoadEnvException;
import team.balam.util.sqlite.connection.PoolManager;
import team.balam.util.sqlite.connection.vo.QueryTimeoutException;
import team.balam.util.sqlite.connection.vo.QueryVo;
import team.balam.util.sqlite.connection.vo.QueryVoFactory;
import team.balam.util.sqlite.connection.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * Service.xml 의 정보를 db 에 저장해 준다.
 * Service 를 reload 하기 위해서 저장하며 서버가 재기동 될 때 마다 초기화 된다.
 */
public class ServiceInfoDao {
	public static void createTable() throws Exception {
		initServiceDirectoryTable();
		initServiceVariableTable();
		initServiceTable();
		initScheduleTable();
	}

	private static void initServiceDirectoryTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS SERVICE_DIRECTORY(" +
				"PATH TEXT NOT NULL, " +
				"CLASS TEXT NOT NULL," +
				"PRIMARY KEY(PATH))";
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.EXECUTE);
		vo.setQuery(query);

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}

		vo = QueryVoFactory.create(QueryVo.Type.DELETE);
		vo.setQuery("DELETE FROM SERVICE_DIRECTORY");

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}
	}

	private static void initServiceVariableTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS SERVICE_VARIABLE(" +
				"SERVICE_DIRECTORY_PATH TEXT NOT NULL, " +
				"SERVICE TEXT, " +
				"KEY TEXT, " +
				"VALUE TEXT," +
				"PRIMARY KEY(SERVICE_DIRECTORY_PATH, SERVICE))";
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.EXECUTE);
		vo.setQuery(query);

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}

		vo = QueryVoFactory.create(QueryVo.Type.DELETE);
		vo.setQuery("DELETE FROM SERVICE_DIRECTORY");

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}
	}

	private static void initServiceTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS SERVICE(" +
				"PATH TEXT, " +
				"CLASS TEXT NOT NULL, " +
				"SERVICE_VARIABLE TEXT, " +
				"PRIMARY KEY(PATH))";
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.EXECUTE);
		vo.setQuery(query);

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}

		vo = QueryVoFactory.create(QueryVo.Type.DELETE);
		vo.setQuery("DELETE FROM SERVICE");

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}
	}

	private static void initScheduleTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS SCHEDULE(" +
				"ID TEXT, " +
				"SERVICE_PATH TEXT, " +
				"CRON TEXT, " +
				"DUPLICATE_EXECUTION TEXT, " +
				"USE TEXT, " +
				"INIT_EXECUTION TEXT, " +
				"PRIMARY KEY(ID))";
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.EXECUTE);
		vo.setQuery(query);

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}

		vo = QueryVoFactory.create(QueryVo.Type.DELETE);
		vo.setQuery("DELETE FROM SERVICE");

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}
	}

	public static void insertServiceDirectory(String _path, String _class) throws LoadEnvException {
		String query = "INSERT INTO SERVICE_DIRECTORY(PATH, CLASS) VALUES (?, ?)";
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.INSERT);
		vo.setQuery(query);
		vo.setParam(new Object[]{_path, _class});

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);

		try {
			if (!vo.getResult().isSuccess()) {
				throw new LoadEnvException("fail insert to SERVICE_DIRECTORY table.");
			}
		} catch (QueryTimeoutException e) {
			throw new LoadEnvException("query time out.", e);
		}
	}

	public static void insertServiceVariable(String _directoryPath, String _service, String _key, String _value) throws LoadEnvException {
		String query = "INSERT INTO SERVICE_VARIABLE(SERVICE_DIRECTORY_PATH, SERVICE, KEY, VALUE) VALUES (?, ?, ?, ?)";
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.INSERT);
		vo.setQuery(query);
		vo.setParam(new Object[]{_directoryPath, _service, _key, _value});

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);

		try {
			if (!vo.getResult().isSuccess()) {
				throw new LoadEnvException("fail insert to SERVICE_VARIABLE table.");
			}
		} catch (QueryTimeoutException e) {
			throw new LoadEnvException("query time out.", e);
		}
	}

	public static void insertSchedule(String _id, String _servicePath, String _cron, String _duplicateExecution, String _use, String _initExecution)
			throws LoadEnvException {
		String query = "INSERT INTO SCHEDULE(ID, SERVICE_PATH, CRON, DUPLICATE_EXECUTION, USE, INIT_EXECUTION) VALUES (?, ?, ?, ?, ?)";
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.INSERT);
		vo.setQuery(query);
		vo.setParam(new Object[]{_id, _servicePath, _cron, _duplicateExecution, _use, _initExecution});

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);

		try {
			if (!vo.getResult().isSuccess()) {
				throw new LoadEnvException("fail insert to SCHEDULE table.");
			}
		} catch (QueryTimeoutException e) {
			throw new LoadEnvException("query time out.", e);
		}
	}

	public static SchedulerInfo selectSchedule(String _id) throws Exception {
		String query = "SELECT * FROM SCHEDULE WHERE ID=?";
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.SELECT);
		vo.setQuery(query);
		vo.setParam(new Object[]{_id});

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);

		Result result = null;

		try {
			result = vo.getResult();
			List<Map<String, Object>> resultList = result.getSelectResult();

			if (!resultList.isEmpty()) {
				return new SchedulerInfo(resultList.get(0));
			} else {
				return SchedulerInfo.NULL_OBJECT;
			}
		} finally {
			if (result != null) {
				result.close();
			}
		}
	}
}
