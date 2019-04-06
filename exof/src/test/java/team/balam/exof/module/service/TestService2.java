package team.balam.exof.module.service;

import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;

import java.io.IOException;

@ServiceDirectory("/")
public class TestService2 {
    @Service("lecture")
    public void sendLecturePage() throws IOException {

    }
}
