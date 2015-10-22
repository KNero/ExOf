package balam.exof.environment;

import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import balam.exof.scheduler.SchedulerManager;

public class FileModifyChecker extends Observable
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Path envDirPath;
	
	public FileModifyChecker()
	{
		this.addObserver(SchedulerManager.getInstance());
	}
	
	public void start() throws Exception
	{
		//파일이 수정될 경우 모든 옵저버들에게 알려준다.
		String envPath = SystemSetting.getInstance().getString(Setting.PreFix.FRAMEWORK, EnvKey.HOME);
		this.envDirPath = Paths.get(envPath);
		
		WatchService ws = FileSystems.getDefault().newWatchService();
		this.envDirPath.register(ws, StandardWatchEventKinds.ENTRY_MODIFY);
		
		Thread t = new Thread(() -> {
			while(true)
			{
				try
				{
					final WatchKey wk = ws.poll(1, TimeUnit.SECONDS);
					if(wk != null)
					{
						List<WatchEvent<?>> events = wk.pollEvents();
						for(WatchEvent<?> event : events)
						{
							final Path changed = (Path)event.context();
							if(changed.endsWith("service.yaml"))
							{
								FileInputStream fis = null;
								
								try
								{
									fis = new FileInputStream(envPath + "/" + "service.yaml");
									Yaml yaml = new Yaml();
									Object contents = yaml.load(fis);
									
									this.setChanged();
									this.notifyObservers(contents);
								}
								finally
								{
									if(fis != null) fis.close();
								}
							}
						}
						
						wk.reset();
					}
				}
				catch(Exception e)
				{
					this.logger.error("Can not check file.", e);
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
}
