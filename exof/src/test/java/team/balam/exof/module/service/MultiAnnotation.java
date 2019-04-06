package team.balam.exof.module.service;

import org.junit.Test;
import team.balam.exof.Constant;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.util.sqlite.connection.DatabaseLoader;

public class MultiAnnotation {
    /**
     * group id 가 다르고 path 가 같을 경우 ServiceAlreadyExistsException 이 발생하는 것을 확인하기 위한 테스트
     */
    @Test
    public void test() throws Exception {
        DatabaseLoader.load(Constant.ENV_DB, "./test-workspace/env.db");
        ServiceInfoDao.initTable();

        ServiceInfoDao.insertServiceDirectory("/", "team.balam.exof.module.service.TestService2");
        ServiceInfoDao.insertServiceDirectory("/lecture", "team.balam.exof.module.service.TestService1");
        ServiceProvider.getInstance().loadServiceDirectory();

        // service group id 를 지정하지 않았기 때문에 service name 과 같은 값으로 세팅된다.
        ServiceProvider.lookup(new ServiceObject("/lecture"));

        ServiceObject serviceObject = new ServiceObject("/lecture");
        serviceObject.setServiceGroupId("GET");
        ServiceProvider.lookup(serviceObject);
    }
}
