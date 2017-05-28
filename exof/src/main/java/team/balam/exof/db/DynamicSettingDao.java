package team.balam.exof.db;

import java.util.List;
import java.util.Map;

import team.balam.util.sqlite.connection.DatabaseLoader;
import team.balam.util.sqlite.connection.PoolManager;
import team.balam.util.sqlite.connection.vo.QueryVo;
import team.balam.util.sqlite.connection.vo.QueryVo.Type;
import team.balam.util.sqlite.connection.vo.QueryVoFactory;
import team.balam.util.sqlite.connection.vo.Result;

public class DynamicSettingDao {
	private static final String DB_NAME = "./env/env.db";
	
	private DynamicSettingDao() {
	}
	
	public static void createTable() throws Exception {
		DatabaseLoader.load(DB_NAME, DB_NAME);
		
		String query = "CREATE TABLE IF NOT EXISTS ENVIRONMENT("
				+ "NAME TEXT, VALUE TEXT NOT NULL, DESCRIPTION TEXT, PRIMARY KEY(NAME))";
		QueryVo vo = QueryVoFactory.create(Type.EXECUTE);
		vo.setQuery(query);
		
		PoolManager.getInstance().executeQuery(DB_NAME, vo);
		
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}
	}
	
	public static void insert(String _name, String _value, String _description) throws Exception {
		QueryVo vo = QueryVoFactory.create(Type.INSERT);
		vo.setQuery("INSERT INTO ENVIRONMENT VALUES(?, ?, ?)");
		vo.setParam(new Object[]{_name, _value, _description});
		
		PoolManager.getInstance().executeQuery(DB_NAME, vo);
		
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}
	}
	
	public static Map<String, Object> select(String _name) throws Exception {
		QueryVo vo = QueryVoFactory.create(Type.SELECT);
		vo.setQuery("SELECT VALUE, DESCRIPTION FROM ENVIRONMENT WHERE NAME=?");
		vo.setParam(new Object[]{_name});
		
		PoolManager.getInstance().executeQuery(DB_NAME, vo);
		
		Result result = vo.getResult();
		
		try
		{
			if (result.isSuccess()) {
				if (result.getSelectResult().size() > 0) {
					return result.getSelectResult().get(0);
				} else {
					return null;
				}
			} else {
				throw result.getException();
			}
		} finally {
			if (result != null) {
				result.close();
			}
		}
	}
	
	public static List<Map<String, Object>> selectList(String _name) throws Exception {
		QueryVo vo = QueryVoFactory.create(Type.SELECT);
		vo.setQuery("SELECT NAME, VALUE, DESCRIPTION FROM ENVIRONMENT WHERE NAME LIKE ? ORDER BY NAME");
		vo.setParam(new Object[]{"%" + _name + "%"});
		
		PoolManager.getInstance().executeQuery(DB_NAME, vo);
		
		Result result = vo.getResult();
		
		try
		{
			if (result.isSuccess()) {
				return result.getSelectResult();
			} else {
				throw result.getException();
			}
		} finally {
			if (result != null) {
				result.close();
			}
		}
	}
	
	public static void update(String _name, String _value, String _description) throws Exception {
		QueryVo vo = QueryVoFactory.create(Type.UPDATE);
		vo.setQuery("UPDATE ENVIRONMENT SET VALUE=?, DESCRIPTION=? WHERE NAME=?");
		vo.setParam(new Object[]{_value, _description, _name});
		
		PoolManager.getInstance().executeQuery(DB_NAME, vo);
		
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}
	}
	
	public static void delete(String _name) throws Exception {
		QueryVo vo = QueryVoFactory.create(Type.DELETE);
		vo.setQuery("DELETE FROM ENVIRONMENT WHERE NAME=?");
		vo.setParam(new Object[]{_name});
		
		PoolManager.getInstance().executeQuery(DB_NAME, vo);
		
		if (!vo.getResult().isSuccess()) {
			throw vo.getResult().getException();
		}
	}
}
