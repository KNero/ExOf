package team.balam.exof.module.deploy;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.ExternalClassLoader;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.LoadEnvException;
import team.balam.exof.environment.ServiceLoader;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.component.HttpGet;
import team.balam.exof.module.service.component.HttpPost;
import team.balam.exof.util.HttpResponseBuilder;
import team.balam.exof.util.StreamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@ServiceDirectory
public class AutoDeployService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeployService.class);

	@Service(name = "library")
	@Inbound(classObject = HttpPost.class)
	public FullHttpResponse saveExternalLibrary(FullHttpRequest _request) {
		HttpDataFactory dataFactory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);
		HttpPostRequestDecoder requestDecoder = new HttpPostRequestDecoder(dataFactory, _request);
		FileUpload libraryData = (FileUpload) requestDecoder.getBodyHttpData("library");

		try {
			File file = libraryData.getFile();

			String home = SystemSetting.getFramework(EnvKey.HOME);
			File external = new File(home + "/lib/external/" + libraryData.getFilename());
			StreamUtil.write(new FileInputStream(file), new FileOutputStream(external));
		} catch(IOException e) {
			String error = "Fail to save library file. " + libraryData.getFilename();
			LOGGER.error(error, e);
			return HttpResponseBuilder.buildServerError(error);
		}

		return HttpResponseBuilder.buildOk("success");
	}

	@Service(name = "service")
	@Inbound(classObject = HttpGet.class)
	public FullHttpResponse reloadService() {
		String home = SystemSetting.getFramework(EnvKey.HOME);
		ServiceLoader serviceLoader = new ServiceLoader();

		try {
			serviceLoader.load(home + "/env");
			new Deploy().insertDeployService();
		} catch (LoadEnvException e) {
			LOGGER.error("Can not load service for auto deploy.", e);
			return HttpResponseBuilder.buildServerError("Fail to read service.xml");
		}

		try {
			ExternalClassLoader.load(home + "/lib/external");
		} catch(FileNotFoundException e) {
			LOGGER.error("Not exists external library folder.", e);
			return HttpResponseBuilder.buildServerError("Not exists external library folder. " + home + "/lib/external");
		}

		ServiceProvider.getInstance().loadServiceDirectory();

		return HttpResponseBuilder.buildOk("success");
	}
}
