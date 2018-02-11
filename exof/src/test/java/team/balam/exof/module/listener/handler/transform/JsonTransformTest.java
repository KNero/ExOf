package team.balam.exof.module.listener.handler.transform;

import org.junit.Assert;
import org.junit.Test;
import team.balam.exof.module.service.ServiceObject;

import java.io.IOException;

/**
 * Created by smkwon on 2018-02-01.
 */
public class JsonTransformTest extends AbstractJsonTransform<TestValue> {
    public JsonTransformTest() {
        super(TestValue.class);
    }

    @Override
    protected String getServicePath(TestValue testValue) {
        return testValue.getServicePath();
    }

    @Test
    public void test() throws IOException {
        JsonTransformTest test = new JsonTransformTest();
        ServiceObject serviceObject = test.transform("{\"servicePath\":\"/test\", \"a\":\"a1\", \"b\":123}");
        TestValue testValue = (TestValue) serviceObject.getRequest();
        Assert.assertEquals("/test", testValue.getServicePath());
        Assert.assertEquals("a1", testValue.getA());
        Assert.assertEquals(123, testValue.getB());
    }
}

class TestValue {
    private String servicePath;
    private String a;
    private int b;

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
