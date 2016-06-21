package team.balam.exof.db;

public class DatasourceNotLoadException extends Exception 
{
	private static final long serialVersionUID = -1486064383501945740L;

	public DatasourceNotLoadException(String _datasource)
	{
		super("Can not find the Datasource[" + _datasource + "].");
	}
}
