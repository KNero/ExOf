package balam.exof.environment;

public class MainLoader implements Loader
{
	private Loader[] loaders = new Loader[]{new FrameworkLoader()};
	
	@Override
	public void load() throws LoadEnvException 
	{
		String home = System.getProperty("exofHome", "./env");
		SystemSetting.getInstance().set(Setting.PreFix.FRAMEWORK, "home", home);
		
		for(Loader loader : this.loaders)
		{
			loader.load();
		}
	}
}
