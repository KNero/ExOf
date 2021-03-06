package team.balam.exof.module.deploy;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.ExternalClassLoader;
import team.balam.exof.container.SchedulerManager;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.component.http.HttpPost;
import team.balam.exof.util.HttpResponseBuilder;
import team.balam.exof.util.StreamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@ServiceDirectory
public class AutoDeployService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeployService.class);

	@Service(name = "library")
	@Inbound({IdPasswordChecker.class, HttpPost.class})
	public FullHttpResponse saveExternalLibrary(FullHttpRequest _request) {
		HttpDataFactory dataFactory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);
		HttpPostRequestDecoder requestDecoder = new HttpPostRequestDecoder(dataFactory, _request);
		FileUpload libraryData = (FileUpload) requestDecoder.getBodyHttpData("library");

		try {
			File file = libraryData.getFile();

			String home = SystemSetting.getFramework(EnvKey.HOME);
			File external = new File(home + "/lib/external/" + libraryData.getFilename());
			File backupExternal = new File(external.getAbsolutePath() + ".back");

			StreamUtil.write(new FileInputStream(external), new FileOutputStream(backupExternal)); // backup
			StreamUtil.write(new FileInputStream(file), new FileOutputStream(external)); // change
		} catch(IOException e) {
			String error = "Fail to save library file. " + libraryData.getFilename();
			LOGGER.error(error, e);
			return HttpResponseBuilder.buildServerError(error);
		}

		return HttpResponseBuilder.buildOk("success");
	}

	@Service(name = "service")
	@Inbound({IdPasswordChecker.class, HttpPost.class})
	public FullHttpResponse reloadService() {
		String home = SystemSetting.getFramework(EnvKey.HOME);

		try {
			SchedulerManager.getInstance().stop();
			ServiceProvider.getInstance().loadServiceDirectory();
			LOGGER.warn("Service and Scheduler is stopped by reload request.");
		} catch (Exception e) {
			LOGGER.error("[FATAL] FAIL to stop custom system.", e);
			return HttpResponseBuilder.buildServerError("Fail to stop custom system.");
		}

        ExternalClassLoader.load(home + "/lib/external");

		try {
			SchedulerManager.getInstance().start();
			SchedulerManager.getInstance().executeInitTimeAndStart();
			LOGGER.warn("Service and Scheduler is started by reload request.");
		} catch (Exception e) {
			LOGGER.error("[FATAL] FAIL to start custom system.", e);
			return HttpResponseBuilder.buildServerError("Fail to start custom system.");
		}

		return HttpResponseBuilder.buildOk("success");
	}

	@Service(name = "rollback")
	@Inbound({IdPasswordChecker.class, HttpPost.class})
	public FullHttpResponse rollbackLibrary(FullHttpRequest _request) {
		String messageContent = _request.content().toString(Charset.defaultCharset());
		if (messageContent.isEmpty()) {
			return HttpResponseBuilder.buildBadRequest("library parameter(file name) is empty.");
		}

		QueryStringDecoder queryDecoder = new QueryStringDecoder(messageContent);
		Map<String, List<String>> parameters = queryDecoder.parameters();

		List<String> libName = parameters.get("library");
		if (libName.isEmpty()) {
			return HttpResponseBuilder.buildBadRequest("library parameter(file name) is empty.");
		}

		String jarName = libName.get(0);
		String home = SystemSetting.getFramework(EnvKey.HOME);

		File external = new File(home + "/lib/external/" + jarName);
		File backupExternal = new File(external.getAbsolutePath() + ".back");

		try (FileInputStream source = new FileInputStream(backupExternal);
			FileOutputStream target = new FileOutputStream(external)) {
			StreamUtil.write(source, target);
		} catch (IOException e) {
			String error = "Fail to rollback library file. " + jarName;
			LOGGER.error(error, e);
			return HttpResponseBuilder.buildServerError(error);
		}

		return HttpResponseBuilder.buildOk("success");
	}
}
