package team.balam.exof.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.environment.LoadEnvException;
import team.balam.exof.environment.vo.SchedulerInfo;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.environment.vo.ServiceVariable;
import team.balam.util.sqlite.connection.vo.QueryVo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service.xml 의 정보를 db 에 저장해 준다.
 * Service 를 reload 하기 위해서 저장하며 서버가 재기동 될 때 마다 초기화 된다.
 */
public class ServiceInfoDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInfoDao.class);

	private ServiceInfoDao() {

	}

	public static void initTable() throws Exception {
		initServiceDirectoryTable();
		initServiceVariableTable();
		initScheduleTable();
		initLoadedServiceTable();
	}

	private static void initServiceDirectoryTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS SERVICE_DIRECTORY (" +
				"PATH TEXT NOT NULL, " +
				"CLASS TEXT NOT NULL," +
				"PRIMARY KEY(PATH))";
		EnvDbHelper.execute(QueryVo.Type.EXECUTE, query);

		query = "DELETE FROM SERVICE_DIRECTORY";
		EnvDbHelper.execute(QueryVo.Type.DELETE, query);
	}

	private static void initServiceVariableTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS SERVICE_VARIABLE (" +
				"SERVICE_DIRECTORY_PATH TEXT NOT NULL, " +
				"KEY TEXT, " +
				"VALUE TEXT, " +
				"KEY_ORDER NUMBER, " +
				"VALUE_ORDER NUMBER)";
		EnvDbHelper.execute(QueryVo.Type.EXECUTE, query);

		query = "DELETE FROM SERVICE_VARIABLE";
		EnvDbHelper.execute(QueryVo.Type.DELETE, query);
	}

	private static void initScheduleTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS SCHEDULER (" +
				"ID TEXT, " +
				"SERVICE_PATH TEXT, " +
				"CRON TEXT, " +
				"DUPLICATE_EXECUTION TEXT, " +
				"USE TEXT, " +
				"INIT_EXECUTION TEXT, " +
				"PRIMARY KEY(ID))";
		EnvDbHelper.execute(QueryVo.Type.EXECUTE, query);

		query = "DELETE FROM SCHEDULER";
		EnvDbHelper.execute(QueryVo.Type.DELETE, query);
	}

	private static void initLoadedServiceTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS LOADED_SERVICE (" +
				"CLASS TEXT, " +
				"SERVICE_NAME TEXT, " +
				"SERVICE_GROUP TEXT, " +
				"METHOD TEXT)";
		EnvDbHelper.execute(QueryVo.Type.EXECUTE, query);

		query = "DELETE FROM LOADED_SERVICE";
		EnvDbHelper.execute(QueryVo.Type.DELETE, query);
	}

	public static void insertServiceDirectory(String _path, String _class) throws LoadEnvException {
		String selectQuery = "SELECT COUNT(*) AS CNT FROM SERVICE_DIRECTORY WHERE PATH=? AND CLASS=?";
		Object[] selectParam = new Object[]{_path, _class};

		try {
			List<Map<String, Object>> result = EnvDbHelper.select(selectQuery, selectParam);
			int count = (Integer) result.get(0).get("cnt");

			if (count == 0) {
				String query = "INSERT INTO SERVICE_DIRECTORY (PATH, CLASS) VALUES (?, ?)";
				Object[] param = new Object[]{_path, _class};

				EnvDbHelper.execute(QueryVo.Type.INSERT, query, param);
			}
		} catch (Exception e) {
			throw new LoadEnvException("Fail insert to SERVICE_DIRECTORY", e);
		}
	}

	public static void insertServiceVariable(String directoryPath, String key, String value) throws LoadEnvException {
		int maxKeyOrder = selectServiceVariableMaxKeyOrder(directoryPath);
		int maxValueOrder = selectServiceVariableMaxValueOrder(directoryPath, key);

		String query = "INSERT INTO SERVICE_VARIABLE (SERVICE_DIRECTORY_PATH, KEY, VALUE, KEY_ORDER, VALUE_ORDER) VALUES (?, ?, ?, ?, ?)";
		Object[] param = new Object[]{directoryPath, key, value, maxKeyOrder, maxValueOrder};

		try {
			EnvDbHelper.execute(QueryVo.Type.INSERT, query, param);
		} catch (Exception e) {
			throw new LoadEnvException("Fail to insert to SERVICE_VARIABLE", e);
		}
	}

	private static int selectServiceVariableMaxKeyOrder(String directoryPath) throws LoadEnvException {
		String query = "SELECT COUNT(*) AS CNT FROM SERVICE_VARIABLE WHERE SERVICE_DIRECTORY_PATH=?";
		Object[] param = new Object[]{directoryPath};

		try {
			List<Map<String, Object>> result = EnvDbHelper.select(query, param);
			return (Integer) result.get(0).get("cnt");
		} catch (Exception e) {
			throw new LoadEnvException("Fail to select service variable max order.", e);
		}
	}

	private static int selectServiceVariableMaxValueOrder(String directoryPath, String key) throws LoadEnvException {
		String query = "SELECT COUNT(*) AS CNT FROM SERVICE_VARIABLE WHERE SERVICE_DIRECTORY_PATH=? AND KEY=?";
		Object[] param = new Object[]{directoryPath, key};

		try {
			List<Map<String, Object>> result = EnvDbHelper.select(query, param);
			return (Integer) result.get(0).get("cnt");
		} catch (Exception e) {
			throw new LoadEnvException("Fail to select service variable max order.", e);
		}
	}

	public static void insertSchedule(String _id, String _servicePath, String _cron, String _duplicateExecution, String _use, String _initExecution)
			throws LoadEnvException {
		String query = "INSERT INTO SCHEDULER (ID, SERVICE_PATH, CRON, DUPLICATE_EXECUTION, USE, INIT_EXECUTION) VALUES (?, ?, ?, ?, ?, ?)";
		Object[] param = new Object[]{_id, _servicePath, _cron, _duplicateExecution, _use, _initExecution};

		try {
			EnvDbHelper.execute(QueryVo.Type.INSERT, query, param);
		} catch (Exception e) {
			throw new LoadEnvException("Fail insert to SCHEDULER", e);
		}
	}

	public static List<ServiceDirectoryInfo> selectServiceDirectory() {
		String query = "SELECT * FROM SERVICE_DIRECTORY";

		try {
			List<Map<String, Object>> selectList = EnvDbHelper.select(query);

			if (!selectList.isEmpty()) {
				List<ServiceDirectoryInfo> resultList = new ArrayList<>();
				for (Map<String, Object> row : selectList) {
					resultList.add(new ServiceDirectoryInfo(row));
				}

				return resultList;
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}

		return Collections.emptyList();
	}

	public static ServiceDirectoryInfo selectServiceDirectory(String _dirPath) {
		String query = "SELECT * FROM SERVICE_DIRECTORY WHERE PATH=?";

		try {
			List<Map<String, Object>> selectList = EnvDbHelper.select(query, _dirPath);

			if (!selectList.isEmpty()) {
				return new ServiceDirectoryInfo(selectList.get(0));
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}

		return ServiceDirectoryInfo.NULL_OBJECT;
	}

	public static void deleteServiceDirectory(String _path) {
		String query = "DELETE FROM SERVICE_DIRECTORY WHERE PATH=?";
		Object[] param = new Object[]{_path};

		try {
			EnvDbHelper.execute(QueryVo.Type.DELETE, query, param);
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}
	}


	public static ServiceVariable selectServiceVariable(String serviceDirectoryPath) {
		try {
			String query = "SELECT KEY, VALUE FROM SERVICE_VARIABLE WHERE SERVICE_DIRECTORY_PATH=? ORDER BY KEY_ORDER, VALUE_ORDER";

			List<Map<String, Object>> selectList = EnvDbHelper.select(query, serviceDirectoryPath);
			if (!selectList.isEmpty()) {
				return new ServiceVariable(selectList);
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}

		return ServiceVariable.NULL_OBJECT;
	}

	public static void updateServiceVariableValue(String directoryPath, String key, String value) {
		String query = "UPDATE SERVICE_VARIABLE SET VALUE=? WHERE SERVICE_DIRECTORY_PATH=? AND KEY=?";
		Object[] param = new Object[]{value, directoryPath, key};

		try {
			EnvDbHelper.execute(QueryVo.Type.UPDATE, query, param);
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}
	}

	public static SchedulerInfo selectScheduler(String _id) {
		String query = "SELECT * FROM SCHEDULER WHERE ID=?";
		Object[] param = new Object[]{_id};

		try {
			List<Map<String, Object>> resultList = EnvDbHelper.select(query, param);

			if (!resultList.isEmpty()) {
				return new SchedulerInfo(resultList.get(0));
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}

		return SchedulerInfo.NULL_OBJECT;
	}

	public static List<SchedulerInfo> selectScheduler() {
		String query = "SELECT * FROM SCHEDULER";

		try {
			List<Map<String, Object>> selectList = EnvDbHelper.select(query);

			if (!selectList.isEmpty()) {
				List<SchedulerInfo> resultList = new ArrayList<>();
				for (Map<String, Object> row : selectList) {
					resultList.add(new SchedulerInfo(row));
				}

				return resultList;
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}

		return Collections.emptyList();
	}

	public static void updateSchedulerUse(String _id, String _isUse) {
		String query = "UPDATE SCHEDULER SET USE=? WHERE ID=?";
		Object[] param = new Object[]{_isUse, _id};

		try {
			EnvDbHelper.execute(QueryVo.Type.UPDATE, query, param);
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}
	}

	public static void updateSchedulerCron(String _id, String _cron) {
		String query = "UPDATE SCHEDULER SET CRON=? WHERE ID=?";
		Object[] param = new Object[]{_cron, _id};

		try {
			EnvDbHelper.execute(QueryVo.Type.UPDATE, query, param);
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}
	}

	public static void deleteServiceVariable(String serviceDirectoryPath, String key) {
		String query = "DELETE FROM SERVICE_VARIABLE WHERE SERVICE_DIRECTORY_PATH=? AND KEY=?";
		Object[] param = new Object[]{serviceDirectoryPath, key};

		try {
			EnvDbHelper.execute(QueryVo.Type.DELETE, query, param);
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}
	}

	public static void insertLoadedService(String directoryClass, String servicePath, String serviceGroupId, String method) {
		String query = "insert into loaded_service (class, service_name, service_group, method) values (?, ?, ?, ?)";
		Object[] param = new Object[]{directoryClass, servicePath, serviceGroupId, method};

		try {
			EnvDbHelper.execute(QueryVo.Type.INSERT, query, param);
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}
	}

	public static List<LoadedServiceDto> selectServiceFrom(String directoryClass) {
		ArrayList<LoadedServiceDto> result = new ArrayList<>();

		String query = "select * from loaded_service where class=?";
		Object[] param = new Object[]{directoryClass};

		try {
			List<Map<String, Object>> resultList = EnvDbHelper.select(query, param);

			for (Map<String, Object> row : resultList) {
				result.add(new LoadedServiceDto(directoryClass,
						row.get("service_name").toString(),
						row.get("service_group").toString(),
						row.get("method").toString()));
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred execute query.", e);
		}

		return result;
	}
}
