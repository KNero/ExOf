package team.balam.exof.module.deploy;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.environment.TextCrypto;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;
import team.balam.exof.util.HttpResponseBuilder;

public class IdPasswordChecker implements Inbound {
	private String id;
	private String password;

	public IdPasswordChecker() {
		this.id = SystemSetting.getFramework(DeployRequester.DEPLOY_ID);
		this.password = SystemSetting.getFramework(DeployRequester.DEPLOY_PASSWORD);
	}

	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException {
		if (!this.id.isEmpty() && !this.password.isEmpty()) {
			FullHttpRequest request = (FullHttpRequest) _se.getRequest();
			String clientId = request.headers().get(DeployRequester.DEPLOY_ID);
			String clientPassword = request.headers().get(DeployRequester.DEPLOY_PASSWORD);

			if (!this.id.equals(clientId) || !this.password.equals(clientPassword)) {
				FullHttpResponse response = HttpResponseBuilder.buildUnauthorized("Header contains " +
						DeployRequester.DEPLOY_ID + " and " + DeployRequester.DEPLOY_PASSWORD);
				RequestContext.writeAndFlushResponse(response);

				throw new InboundExecuteException("ID or Password is not match.");
			}
		}
	}
}
