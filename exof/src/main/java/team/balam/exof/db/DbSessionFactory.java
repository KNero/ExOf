package team.balam.exof.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DbSessionFactory {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SqlSessionFactory defaultSqlSessionFactory;
    private Map<String, SqlSessionFactory> factoryMap = new ConcurrentHashMap<>();

    private static DbSessionFactory self = new DbSessionFactory();

    private DbSessionFactory() {

    }

    public static DbSessionFactory getInstance() {
        return self;
    }

    public void putSqlSessionFactory(String datasource, SqlSessionFactory factory) {
        factoryMap.put(datasource, factory);
    }

    public SqlSessionFactory getSqlSessionFactory(String _datasource) throws DatasourceNotLoadException {
        SqlSessionFactory factory = this.factoryMap.get(_datasource);

        if (factory == null) {
            throw new DatasourceNotLoadException(_datasource);
        }

        return factory;
    }

    public SqlSessionFactory getDefaultSqlSessionFactory() {
        return this.defaultSqlSessionFactory;
    }

    public SqlSession getDefaultSqlSession() {
        return this.defaultSqlSessionFactory.openSession(false);
    }

    public void setDefaultSqlSessionFactory(SqlSessionFactory _factory) {
        if (this.defaultSqlSessionFactory == null) {
            this.defaultSqlSessionFactory = _factory;
        } else {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("Default sqlSessionFactory was already registered.");
            }
        }
    }

    public SqlSession getSqlSession(String _datasource) throws DatasourceNotLoadException {
        return this.getSqlSessionFactory(_datasource).openSession();
    }

    public SqlSession getSqlSession(String _datasource, boolean _isAutoCommit) throws DatasourceNotLoadException {
        return this.getSqlSessionFactory(_datasource).openSession(_isAutoCommit);
    }
}
