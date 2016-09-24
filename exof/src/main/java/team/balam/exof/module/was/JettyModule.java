package team.balam.exof.module.was;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import team.balam.exof.Module;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;

public class JettyModule implements Module
{
	private Server server;
	
	@Override
	public void start() throws Exception 
	{
		boolean isUse = (boolean)SystemSetting.getInstance().get(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.JETTY_USE);
		if(isUse)
		{
			Integer httpPort = (Integer)SystemSetting.getInstance().get(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.JETTY_HTTP);
			Integer httpsPort = (Integer)SystemSetting.getInstance().get(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.JETTY_HTTPS);
			
			if(httpPort == null && httpsPort == null)
			{
				throw new Exception("Http port and https port is empty.");
			}
			
			this.server = new Server();
			
			Integer maxIdleTime = (Integer)SystemSetting.getInstance().get(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.JETTY_MAX_IDLE);
			Integer headerSize = (Integer)SystemSetting.getInstance().get(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.JETTY_HEADER_SIZE);
			
			if(httpPort != null)
			{
				SelectChannelConnector connector = new SelectChannelConnector();
				connector.setPort(httpPort);
				connector.setMaxIdleTime(maxIdleTime);
				connector.setRequestHeaderSize(headerSize);
				this.server.addConnector(connector);
			}
			
			if(httpsPort != null)
			{
				String sslCtxClass = SystemSetting.getInstance().getString(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.JETTY_SSL_CONTEXT);
				SslContextFactoryBuilder sslCtxFactoryBuilder = (SslContextFactoryBuilder)Class.forName(sslCtxClass).newInstance();
				SslContextFactory sslCtxFactory = sslCtxFactoryBuilder.build();
				
				SslSelectChannelConnector sslConnector = new SslSelectChannelConnector(sslCtxFactory);
				sslConnector.setPort(httpsPort);
				sslConnector.setMaxIdleTime(maxIdleTime);
				sslConnector.setRequestHeaderSize(headerSize);
				this.server.addConnector(sslConnector);
			}
			
			String descriptor = SystemSetting.getInstance().getString(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.JETTY_DESCRIPTOR);
			String resourceBase = SystemSetting.getInstance().getString(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.JETTY_RESOURCE_BASE);
			String contextPath = SystemSetting.getInstance().getString(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.JETTY_CONTEXT_PATH);
			
			WebAppContext webapp = new WebAppContext();
			webapp.setDescriptor(descriptor);
			webapp.setResourceBase(resourceBase);
			webapp.setContextPath(contextPath);
			this.server.setHandler(webapp);
			
			this.server.start();
		}
	}

	@Override
	public void stop() throws Exception 
	{
		if(this.server != null)
		{
			this.server.stop();
		}
	}
}
