package team.balam.exof.db;

import team.balam.exof.Constant;
import team.balam.util.sqlite.connection.PoolManager;
import team.balam.util.sqlite.connection.vo.QueryVo;
import team.balam.util.sqlite.connection.vo.QueryVoFactory;
import team.balam.util.sqlite.connection.vo.Result;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class EnvDbHelper {
	private EnvDbHelper() {

	}

	static List<Map<String, Object>> select(String query, Object... param) throws Exception {
		QueryVo vo = QueryVoFactory.create(QueryVo.Type.SELECT);
		vo.setQuery(query);
		vo.setParam(param);

		PoolManager.executeQuery(Constant.ENV_DB, vo);

		Result result = vo.getResult();

		try {
			return vo.getResult().getSelectResult();
		} finally {
			if (result != null) {
				result.close();
			}
		}
	}

	static Map<String, Object> selectOne(String query, Object... param) throws Exception {
	    List<Map<String, Object>> result = select(query, param);

	    if (!result.isEmpty()) {
	        return result.get(0);
        } else {
	        return Collections.emptyMap();
        }
    }

	static void execute(QueryVo.Type _type, String _query, Object... _param) throws Exception {
		QueryVo vo = QueryVoFactory.create(_type);
		vo.setQuery(_query);
		vo.setParam(_param);

		PoolManager.executeQuery(Constant.ENV_DB, vo);

		Result result = vo.getResult();

		if (!result.isSuccess()) {
			throw result.getException();
		}
	}
}
