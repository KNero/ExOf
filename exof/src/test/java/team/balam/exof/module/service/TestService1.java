package team.balam.exof.module.service;

import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.component.http.HttpMethod;
import team.balam.exof.module.service.component.http.RestService;

@ServiceDirectory("/lecture")
public class TestService1 {
    @RestService(method = HttpMethod.GET)
    public void test() {}

}
