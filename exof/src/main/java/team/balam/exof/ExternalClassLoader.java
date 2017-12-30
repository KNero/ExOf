package team.balam.exof;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * lib/external 폴더 안의 jar 파일의 class 를 관리한다.
 */
public class ExternalClassLoader {
    private static Logger LOG = LoggerFactory.getLogger(ExternalClassLoader.class);
	private static URLClassLoader external;

	/**
	 * 폴더 안의 모든 jar 를 로딩한다.
	 * @param _externalPath 로딩할 폴더의 path
	 */
	public static void load(String _externalPath) {
		List<URL> urlList = new ArrayList<>();

		File extFile = new File(_externalPath);
		if (extFile.exists()) {
			for (String file : extFile.list()) {
				File jar = new File(extFile, file);

				if (jar.isFile() && file.endsWith(".jar")) {
					try {
					    LOG.info("");
						urlList.add(jar.toURI().toURL());
					} catch(MalformedURLException e) {
					}
				} else if (jar.isDirectory()) {
					load(jar.getAbsolutePath());
				}
			}

			URL[] urls = new URL[urlList.size()];
			external = new URLClassLoader(urlList.toArray(urls));
		} else {
            LOG.warn("Not exists EXTERNAL LIBRARY folder. " + extFile.getAbsolutePath());
		}
	}

	public static ClassLoader getClassLoader() {
		return external;
	}

	public static Class<?> loadClass(String _class) throws ClassNotFoundException {
		return external.loadClass(_class);
	}
}
