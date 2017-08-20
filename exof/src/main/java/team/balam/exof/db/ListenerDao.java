package team.balam.exof.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.Constant;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.LoadEnvException;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.util.sqlite.connection.vo.QueryVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ListenerDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListenerDao.class);

	private ListenerDao() {

	}

	public static void initTable() throws Exception {
		initPortAttribute();
		initChildElementsTable();
	}

	private static void initPortAttribute() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS PORT_ATTRIBUTE (" +
				"PORT INT NOT NULL," +
				"KEY TEXT NOT NULL," +
				"VALUE TEXT," +
				"PRIMARY KEY (PORT, KEY))";
		EnvDbHelper.execute(QueryVo.Type.EXECUTE, query, null);

		query = "DELETE FROM PORT_ATTRIBUTE";
		EnvDbHelper.execute(QueryVo.Type.DELETE, query, null);
	}

	private static void initChildElementsTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS PORT_CHILD_ATTRIBUTE (" +
				"PORT INT NOT NULL," +
				"NODE_NAME TEXT NOT NULL," +
				"KEY TEXT TEXT NOT NULL," +
				"VALUE TEXT," +
				"PRIMARY KEY (PORT, NODE_NAME, KEY))";
		EnvDbHelper.execute(QueryVo.Type.EXECUTE, query, null);


		query = "DELETE FROM PORT_CHILD_ATTRIBUTE";
		EnvDbHelper.execute(QueryVo.Type.DELETE, query, null);
	}

	public static void insertPortAttribute(int _port, String _key, String _value) throws LoadEnvException {
		String query = "INSERT INTO PORT_ATTRIBUTE (PORT, KEY, VALUE) VALUES (?, ?, ?)";
		Object[] param = new Object[]{_port, _key, _value};

		try {
			EnvDbHelper.execute(QueryVo.Type.INSERT, query, param);
		} catch (Exception e) {
			throw new LoadEnvException("Fail to insert PORT_ATTRIBUTE.", e);
		}
	}

	public static void insertChildNode(int _port, String _nodeName, String _key, String _value) throws LoadEnvException {
		String query = "INSERT INTO PORT_CHILD_ATTRIBUTE (PORT, NODE_NAME, KEY, VALUE) VALUES (?, ?, ?, ?)";
		Object[] param = new Object[]{_port, _nodeName, _key, _value};

		try {
			EnvDbHelper.execute(QueryVo.Type.INSERT, query, param);
		} catch (Exception e) {
			throw new LoadEnvException("Fail to insert PORT_CHILD_ATTRIBUTE.", e);
		}
	}

	public static List<PortInfo> selectPortList() {
		String query  = "SELECT PORT FROM PORT_ATTRIBUTE GROUP BY PORT";

		try {
			List<Map<String, Object>> list = EnvDbHelper.select(query, null);

			List<PortInfo> result = new ArrayList<>();
			for (Map<String, Object> m : list) {
				result.add(new PortInfo((Integer) m.get("port")));
			}

			return result;
		} catch (Exception e) {
			LOGGER.error("Can not get port number list.", e);
			return Collections.emptyList();
		}
	}

	public static List<Map<String, Object>> selectAllPortAttribute(int _port) {
		String query = "SELECT VALUE FROM PORT_ATTRIBUTE WHERE PORT=?";
		Object[] param = new Object[]{_port};

		try {
			return EnvDbHelper.select(query, param);
		} catch (Exception e) {
			LOGGER.error("Can not get port attribute value.", e);
			return Collections.emptyList();
		}
	}

	public static String selectPortAttribute(int _port, String _key) {
		String query = "SELECT VALUE FROM PORT_ATTRIBUTE WHERE PORT=? AND KEY=?";
		Object[] param = new Object[]{_port, _key};

		try {
			List<Map<String, Object>> list = EnvDbHelper.select(query, param);
			if (!list.isEmpty()) {
				return (String) list.get(0).get("value");
			}
		} catch (Exception e) {
			LOGGER.error("Can not get port attribute value.", e);
		}

		return Constant.EMPTY_STRING;
	}

	public static String selectChildAttribute(int _port, String _nodeName, String _key) {
		String query = "SELECT VALUE FROM PORT_CHILD_ATTRIBUTE WHERE PORT=? AND NODE_NAME=? AND KEY=?";
		Object[] param = new Object[]{_port, _nodeName, _key};

		try {
			List<Map<String, Object>> list = EnvDbHelper.select(query, param);
			if (!list.isEmpty()) {
				return (String) list.get(0).get("value");
			}
		} catch (Exception e) {
			LOGGER.error("Can not get port child attribute value.", e);
		}

		return Constant.EMPTY_STRING;
	}

	public static PortInfo selectConsolePort() {
		return selectSpecialPort(EnvKey.Listener.CONSOLE);
	}

	public static PortInfo selectAdminConsolePort() {
		return selectSpecialPort(EnvKey.Listener.ADMIN_CONSOLE);
	}

	public static PortInfo selectJettyModule() {
		return selectSpecialPort(EnvKey.Listener.JETTY);
	}

	private static PortInfo selectSpecialPort(String _key) {
		String query = "SELECT PORT FROM PORT_ATTRIBUTE WHERE KEY=? AND VALUE=?";
		Object[] param = new Object[]{_key, Constant.YES};

		try {
			List<Map<String, Object>> list = EnvDbHelper.select(query, param);
			if (list.size() == 1) {
				return new PortInfo((Integer) list.get(0).get("port"));
			} else if (list.size() > 1) {
				throw new Exception(_key.toUpperCase() + " port is must one. count > 1");
			}
		} catch (Exception e) {
			LOGGER.error("Can not get port.", e);
		}

		return PortInfo.NULL_OBJECT;
	}

	/**
	 * 특수한 기능을 사용하기 위한 포트를 확인해 준다.
	 * 특수한 포트가 아닐 경우 ServerPort 에 의해서 오픈된다.
	 * @param _number
	 * @return
	 */
	public static boolean isSpecialPort(int _number) {
		String query = "SELECT COUNT(PORT) AS CNT FROM PORT_ATTRIBUTE WHERE PORT=? AND KEY IN (? , ?, ?)";
		Object[] param = new Object[]{_number,
				EnvKey.Listener.CONSOLE, EnvKey.Listener.ADMIN_CONSOLE, EnvKey.Listener.JETTY};

		try {
			List<Map<String, Object>> list = EnvDbHelper.select(query, param);
			return (Integer) list.get(0).get("cnt") > 0;
		} catch (Exception e) {
			LOGGER.error("Can not get port.", e);
			return false;
		}
	}

	public static List<Map<String, Object>> selectPortAttribute(int _number) {
		String query = "SELECT KEY, VALUE FROM PORT_ATTRIBUTE WHERE PORT=?";
		Object[] param = new Object[]{_number};

		try {
			return EnvDbHelper.select(query, param);
		} catch (Exception e) {
			LOGGER.error("Can not get port info.", e);
			return Collections.emptyList();
		}
	}

	public static List<Map<String, Object>> selectChildAttribute(int _number) {
		String query = "SELECT NODE_NAME, KEY, VALUE FROM PORT_CHILD_ATTRIBUTE WHERE PORT=?";
		Object[] param = new Object[]{_number};

		try {
			return EnvDbHelper.select(query, param);
		} catch (Exception e) {
			LOGGER.error("Can not get port info.", e);
			return Collections.emptyList();
		}
	}
}
