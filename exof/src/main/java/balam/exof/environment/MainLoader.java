package balam.exof.environment;

public class MainLoader implements Loader
{
	private Loader[] loaders = new Loader[]{new FrameworkLoader(), new ServiceLoader()};
	
	@Override
	public void load(String _envPath) throws LoadEnvException 
	{
		for(Loader loader : this.loaders)
		{
			loader.load(_envPath);
		}
	}
}
