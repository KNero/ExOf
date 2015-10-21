package balam.exof.environment;

public interface Loader 
{
	void load(String _envPath) throws LoadEnvException;
}
