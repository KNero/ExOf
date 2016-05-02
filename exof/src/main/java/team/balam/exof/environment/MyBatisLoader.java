package team.balam.exof.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import team.balam.exof.db.SqlSessionFactory;

public class MyBatisLoader implements Loader
{
	@Override
	public void load(String _envPath) throws LoadEnvException 
	{
		InputStream inputStream = null;
		
		try
		{
			String resource = _envPath + "/mybatis-config.xml";
			inputStream = new FileInputStream(new File(resource));
			org.apache.ibatis.session.SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			
			SqlSessionFactory.getInstance().setSqlSessionFactory(sqlSessionFactory);
			
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
