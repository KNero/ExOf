package team.balam.exof.module.listener.handler.transform;

import java.util.Map;

/**
 * Created by smkwon on 2018-02-01.
 */
public class SimpleJsonTransform extends AbstractJsonTransform<Map> {
    private String servicePathKey = "servicePath";

    public SimpleJsonTransform() {
        super(Map.class);
    }

    public void setServicePathKey(String servicePathKey) {
        this.servicePathKey = servicePathKey;
    }

    @Override
    protected String getServicePath(Map map) {
        return (String) map.get(this.servicePathKey);
    }
}
