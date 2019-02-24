package team.balam.exof.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.db.DbSessionFactory;

public class MyBatisLoader implements Loader {
    private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void load(String _envPath) throws LoadEnvException {
		File mybatis = new File(_envPath + "/mybatis-config.xml");
		if (!mybatis.exists() || mybatis.length() == 0) {
		    log.warn("file not found or contents is empty. {}", mybatis.getAbsolutePath());
		    return;
        }

		org.apache.ibatis.session.SqlSessionFactory defaultSqlSessionFactory = this.loadSqlSessionFactory(mybatis, null);
		DbSessionFactory.getInstance().setDefaultSqlSessionFactory(defaultSqlSessionFactory);
	}
	
	public org.apache.ibatis.session.SqlSessionFactory loadSqlSessionFactory(File mybatisConfig, String _datasource) throws LoadEnvException {
		try (InputStream inputStream = new FileInputStream(mybatisConfig)) {
			if(_datasource != null) {
				return new SqlSessionFactoryBuilder().build(inputStream, _datasource);
			} else {
				return new SqlSessionFactoryBuilder().build(inputStream);
			}
		} catch(Exception e) {
			throw new LoadEnvException("mybatis-config.xml", e);
		}
	}
}
