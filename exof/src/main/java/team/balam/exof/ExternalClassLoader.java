package team.balam.exof;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * lib/external 폴더 안의 jar 파일의 class 를 관리한다.
 */
public class ExternalClassLoader {
	private static URLClassLoader external;

	/**
	 * 폴더 안의 모든 jar 를 로딩한다.
	 * @param _externalPath 로딩할 폴더의 path
	 */
	public static void load(String _externalPath) throws FileNotFoundException {
		List<URL> urlList = new ArrayList<>();

		File extFile = new File(_externalPath);
		if (extFile.exists()) {
			for (String file : extFile.list()) {
				File jar = new File(extFile, file);

				if (jar.isFile() && file.endsWith(".jar")) {
					try {
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
			throw new FileNotFoundException(extFile.getAbsolutePath());
		}
	}

	public static Class<?> loadClass(String _class) throws ClassNotFoundException {
		return external.loadClass(_class);
	}
}
