package team.balam.exof;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.LoadEnvException;
import team.balam.exof.environment.Loader;
import team.balam.exof.environment.MainLoader;
import team.balam.exof.environment.SystemSetting;

/**
 * 
 * @author kwonsm
 *
 */
public class App {
	private static Logger logger = LoggerFactory.getLogger(App.class);
	private static AtomicBoolean isShutdown = new AtomicBoolean(false);
	
	public static void start() {
		String homeSetting = System.getProperty(EnvKey.HOME, ".");
		File home = new File(homeSetting);
		SystemSetting.setFramework(EnvKey.HOME, home.getAbsolutePath());

		try {
			ExternalClassLoader.load(home.getAbsolutePath() + "/lib/external");
		} catch(FileNotFoundException e) {
			logger.error("Not exists external library folder.", e);
			return;
		}

		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
			if(App.isShutdown.compareAndSet(false, true))
			{
				Operator.stop();
			}
			}
		});

		_loadEnv(home);

        Operator.init();
        Operator.start();
        
        //main thread 에서 worker 들을 검사한다.
        while(! App.isShutdown.get()) {
        	ThreadWorkerRegister.getInstance().check();
        	
        	try {
				Thread.sleep(1000);
			}
        	catch(InterruptedException e) {
			}
        }
	}

	private static void _loadEnv(File home) {
		try {
			File envFolder = new File(home, "env");
			if (envFolder.exists()) {
				Loader mainLoader = new MainLoader();
				mainLoader.load(envFolder.getAbsolutePath());
			} else {
				throw new FileNotFoundException(envFolder.getAbsolutePath());
			}
		} catch(Exception e) {
			if(e instanceof LoadEnvException) {
				logger.error("Loader error occurred.", e);
			} else {
				logger.error("Fail to load environment.", e);
			}
		}
	}
	
    public static void main(String[] args)
    {
    	App.start();
    }
}
