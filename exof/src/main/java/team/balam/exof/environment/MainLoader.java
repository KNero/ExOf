package team.balam.exof.environment;

public class MainLoader implements Loader
{
	private Loader[] loaders = new Loader[]{new EnvDbLoader(),
											new FrameworkLoader(),
											new ListenerLoader(),
											new ServiceLoader(),
											new MyBatisLoader()};
	
	@Override
	public void load(String _envPath) throws LoadEnvException {
		for(Loader loader : this.loaders) {
			loader.load(_envPath);
		}
	}
}
