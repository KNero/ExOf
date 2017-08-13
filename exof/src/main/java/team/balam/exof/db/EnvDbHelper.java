package team.balam.exof.db;

import team.balam.exof.Constant;
import team.balam.util.sqlite.connection.PoolManager;
import team.balam.util.sqlite.connection.vo.QueryTimeoutException;
import team.balam.util.sqlite.connection.vo.QueryVo;
import team.balam.util.sqlite.connection.vo.QueryVoFactory;
import team.balam.util.sqlite.connection.vo.Result;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class EnvDbHelper {
	private EnvDbHelper() {

	}

	public static void close(Result result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<Map<String, Object>> select(String query, Object[] param) throws Exception {
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.SELECT);
		vo.setQuery(query);
		vo.setParam(param);

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);

		Result result = null;

		try {
			result = vo.getResult();
			if (result.isSuccess()) {
				return result.getSelectResult();
			} else {
				throw result.getException();
			}
		} finally {
			EnvDbHelper.close(result);
		}
	}

	public static int update(String query, Object[] param) throws Exception {
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.UPDATE);
		vo.setQuery(query);
		vo.setParam(param);

		PoolManager.getInstance().executeQuery(Constant.ENV_DB, vo);

		Result result = vo.getResult();

		if (result.isSuccess()) {
			return result.getResultCount();
		} else {
			throw result.getException();
		}
	}
}
