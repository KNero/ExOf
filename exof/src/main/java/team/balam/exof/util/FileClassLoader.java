package team.balam.exof.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class FileClassLoader extends URLClassLoader {

	public FileClassLoader(ClassLoader _parent, URL... _urls) {
		super(_urls, _parent);
	}

	public FileClassLoader(URL... _urls) {
		super(_urls, ClassLoader.getSystemClassLoader());
	}


	/**
	 * jar 파일을 로딩하며 디렉토리가 들어올 경우 반복적으로 내부 파일을 찾아서 로딩한다.
	 * @param _file jar file or directory
	 * @throws IOException url 을 가져올 수 없을 경우 발생한다.
	 */
	public void loadJar(File _file) throws IOException {
		if (_file != null && _file.exists()) {
			for (String fileList : _file.list()) {
				File childFile = new File(_file, fileList);

				if (_file.isDirectory()) {
					this.loadJar(childFile);
				} else if (_file.getName().endsWith(".jar")) {
					this.addURL(_file.toURI().toURL());
				}
			}
		}
	}

	public static void main(String[] a) throws Exception {
		FileClassLoader loader =
				new FileClassLoader(new File("./lib/SQLite-Helper-0.2.0-SNAPSHOT.jar").toURI().toURL());
		loader.close();
	}
}
