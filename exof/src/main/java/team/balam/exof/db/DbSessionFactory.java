package team.balam.exof.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.LoadEnvException;
import team.balam.exof.environment.MyBatisLoader;
import team.balam.exof.environment.SystemSetting;

public class DbSessionFactory 
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SqlSessionFactory defaultSqlSessionFactory;
	private Map<String, SqlSessionFactory> factoryMap = new ConcurrentHashMap<>();
	
	private static DbSessionFactory self = new DbSessionFactory();
	
	private DbSessionFactory()
	{
		
	}
	
	public static DbSessionFactory getInstance()
	{
		return self;
	}
	
	public SqlSessionFactory getDefaultSqlSessionFactory()
	{
		return this.defaultSqlSessionFactory;
	}
	
	public SqlSessionFactory getSqlSessionFactory(String _datasource) throws DatasourceNotLoadException
	{
		SqlSessionFactory factory = this.factoryMap.get(_datasource);
		
		if(factory == null)
		{
			throw new DatasourceNotLoadException(_datasource);
		}
		
		return factory;
	}
	
	public SqlSession getDefaultSqlSession()
	{
		return this.defaultSqlSessionFactory.openSession(false);
	}
	
	public void setDefaultSqlSessionFactory(SqlSessionFactory _factory)
	{
		if(this.defaultSqlSessionFactory == null)
		{
			this.defaultSqlSessionFactory = _factory;
		}
		else
		{
			if(this.logger.isWarnEnabled())
			{
				this.logger.warn("Default sqlSessionFactory was already registered.");
			}
		}
	}
	
	public SqlSession getSqlSession(String _datasource) throws DatasourceNotLoadException
	{
		return this.getSqlSessionFactory(_datasource).openSession();
	}
	
	public SqlSession getSqlSession(String _datasource, boolean _isAutoCommit) throws DatasourceNotLoadException
	{
		return this.getSqlSessionFactory(_datasource).openSession(_isAutoCommit);
	}
	
	public void loadSqlSessionFactory(String _datasource)
	{
		MyBatisLoader loader = new MyBatisLoader();
		String envPath = SystemSetting.getInstance().getString(EnvKey.PreFix.FRAMEWORK, EnvKey.HOME);
		
		try
		{
			SqlSessionFactory factory = loader.loadSqlSessionFactory(envPath, _datasource);
			this.factoryMap.put(_datasource, factory);
		}
		catch(LoadEnvException e)
		{
			this.logger.error("Can not load sqlSessionFactory", e);
		}
	}
}
