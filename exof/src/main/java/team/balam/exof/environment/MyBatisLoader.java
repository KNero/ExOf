package team.balam.exof.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import team.balam.exof.db.DbSessionFactory;

public class MyBatisLoader implements Loader
{
	@Override
	public void load(String _envPath) throws LoadEnvException 
	{
		org.apache.ibatis.session.SqlSessionFactory defaultSqlSessionFactory = this.loadSqlSessionFactory(_envPath, null);
		
		DbSessionFactory.getInstance().setDefaultSqlSessionFactory(defaultSqlSessionFactory);
	}
	
	public org.apache.ibatis.session.SqlSessionFactory loadSqlSessionFactory(String _envPath, String _datasource) throws LoadEnvException
	{
		InputStream inputStream = null;
		
		try
		{
			String resource = _envPath + "/mybatis-config.xml";
			inputStream = new FileInputStream(new File(resource));
			
			if(_datasource != null)
			{
				return new SqlSessionFactoryBuilder().build(inputStream, _datasource);
			}
			else
			{
				return new SqlSessionFactoryBuilder().build(inputStream);
			}
		}
		catch(Exception e)
		{
			throw new LoadEnvException("mybatis-config.xml", e);
		}
		finally
		{
			try
			{
				if(inputStream != null) inputStream.close();
			}
			catch(Exception e)
			{
				throw new LoadEnvException("mybatis-config.xml", e);
			}
		}
	}
}
