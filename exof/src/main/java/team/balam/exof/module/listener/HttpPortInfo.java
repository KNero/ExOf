package team.balam.exof.module.listener;

import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.listener.handler.codec.HttpServerCodec;
import team.balam.exof.module.listener.handler.transform.HttpTransform;

class HttpPortInfo extends PortInfo {
    HttpPortInfo(int _number) {
        super(_number);
    }

    @Override
    public String getChannelHandler() {
        return HttpServerCodec.class.getName();
    }

    @Override
    public String getMessageTransform() {
        return HttpTransform.class.getName();
    }
}
