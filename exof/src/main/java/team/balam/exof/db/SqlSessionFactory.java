package team.balam.exof.db;

public class SqlSessionFactory 
{
	private org.apache.ibatis.session.SqlSessionFactory sqlSessionFactory;
	
	private static SqlSessionFactory self = new SqlSessionFactory();
	
	private SqlSessionFactory()
	{
		
	}
	
	public static SqlSessionFactory getInstance()
	{
		return self;
	}
	
	public org.apache.ibatis.session.SqlSession getSqlSession()
	{
		return this.sqlSessionFactory.openSession();
	}
	
	public void setSqlSessionFactory(org.apache.ibatis.session.SqlSessionFactory _factory)
	{
		if(this.sqlSessionFactory == null)
		{
			this.sqlSessionFactory = _factory;
		}
	}
}
