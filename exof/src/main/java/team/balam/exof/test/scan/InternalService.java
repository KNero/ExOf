package team.balam.exof.test.scan;

import team.balam.exof.module.service.annotation.ServiceDirectory;

@ServiceDirectory(path = "/internal", internal = true)
public class InternalService {
	public String test2() {
		return "test_test2";
	}
}
