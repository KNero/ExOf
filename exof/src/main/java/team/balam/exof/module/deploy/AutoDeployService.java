package team.balam.exof.module.deploy;

import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.LoadEnvException;
import team.balam.exof.environment.ServiceLoader;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.util.HttpResponseBuilder;

@ServiceDirectory
public class AutoDeployService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeployService.class);

	@Service(name = "service")
	@Inbound(classObject = RequestFilter.class)
	public FullHttpResponse reloadService() {
		String home = SystemSetting.getFramework(EnvKey.HOME);
		ServiceLoader serviceLoader = new ServiceLoader();

		try {
			serviceLoader.load(home + "/env");
		} catch (LoadEnvException e) {
			LOGGER.error("Can not load service for auto deploy.", e);
			return HttpResponseBuilder.buildServerError("Fail to read service.xml");
		}

		ServiceProvider.getInstance().loadServiceDirectory();

		return HttpResponseBuilder.buildOk("success");
	}
}
