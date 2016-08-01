package team.balam.exof.environment;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.container.SchedulerManager;
import team.balam.exof.module.service.ServiceProvider;

public class FileModifyChecker
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Path envDirPath;
	
	private FileObservable serviceChecker = new FileObservable();
	
	public FileModifyChecker()
	{
		this.serviceChecker.addObserver(SchedulerManager.getInstance());
		this.serviceChecker.addObserver(ServiceProvider.getInstance());
	}
	
	public void start() throws Exception
	{
		//파일이 수정될 경우 모든 옵저버들에게 알려준다.
		String envPath = SystemSetting.getInstance().getString(EnvKey.PreFix.FRAMEWORK, EnvKey.HOME);
		this.envDirPath = Paths.get(envPath);
		
		WatchService ws = FileSystems.getDefault().newWatchService();
		this.envDirPath.register(ws, StandardWatchEventKinds.ENTRY_MODIFY);
		
		Thread t = new Thread(() -> {
			while(true)
			{
				WatchKey wk = null;
				
				try
				{
					wk = ws.poll(1, TimeUnit.SECONDS);
					if(wk != null)
					{
						List<WatchEvent<?>> events = wk.pollEvents();
						for(WatchEvent<?> event : events)
						{
							final Path changed = (Path)event.context();
							if(changed.toString().endsWith("service.xml"))
							{
								try
								{
									String envHome = SystemSetting.getInstance().getString(EnvKey.PreFix.FRAMEWORK, EnvKey.HOME);
									Loader loader = new ServiceLoader();
									loader.load(envHome);
									
									this.serviceChecker.updateObservers();
								}
								catch(Exception e)
								{
									this.logger.error("file modify checker error.", e);
								}
							}
						}
					}
				}
				catch(Exception e)
				{
					this.logger.error("Can not check file.", e);
				}
				finally
				{
					if(wk != null) wk.reset();
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
}
