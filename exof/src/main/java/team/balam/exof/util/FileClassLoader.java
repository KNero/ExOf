package team.balam.exof.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class FileClassLoader 
{
	public static void loadJar( String _path ) throws Exception
	{
		File f = new File( _path );
		
		if( f.isDirectory() )
		{
			for( String fileName : f.list() )
			{
				FileClassLoader.loadJar( _path + "/" + fileName );
			}
		}
		else if( f.isFile() && f.getName().endsWith( ".jar" ) )
		{
			FileClassLoader.loadFileOrDirectory( f );
		}
	}
	
	public static void loadFileOrDirectory( String _s ) throws Exception
	{
		File f = new File( _s );
		FileClassLoader.loadFileOrDirectory( f );
	}
	
	public static void loadFileOrDirectory( File _f ) throws IOException
	{
		FileClassLoader.loadUrl( _f.toURI( ).toURL( ) );
	}
	
	public static void loadUrl( URL _u ) throws IOException
	{
		URLClassLoader sysloader = ( URLClassLoader ) ClassLoader.getSystemClassLoader( );
		Class< URLClassLoader > sysclass = URLClassLoader.class;
		
		try
		{
			Method method = sysclass.getDeclaredMethod("addURL" , new Class[]{URL.class});
			method.setAccessible( true );
			method.invoke( sysloader , new Object[] { _u } );
		}
		catch( Exception e )
		{
			throw new IOException( "Error, could not add URL to system classloader" );
		}
	}
}
