package team.balam.exof.module.was;

import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import team.balam.exof.Constant;
import team.balam.exof.Module;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.listener.PortInfo;

public class JettyModule implements Module
{
	private PortInfo portInfo;
	private Server server;
	
	@Override
	public void start() throws Exception 
	{
		List<PortInfo> portList = SystemSetting.getInstance().getList(EnvKey.PreFix.LISTENER, EnvKey.Listener.PORT);
		portList.forEach(_portInfo -> {
			if(Constant.YES.equals(_portInfo.getAttribute(EnvKey.Listener.JETTY)))
			{
				this.portInfo = _portInfo;
			}
		});
		
		if(this.portInfo != null)
		{
			int http = this.portInfo.getAttributeToInt(EnvKey.Listener.HTTP, 0);
			int https = this.portInfo.getAttributeToInt(EnvKey.Listener.HTTPS, 0);
			
			if(http == 0 && https == 0)
			{
				throw new Exception("Http port and https port is empty.");
			}
			
			this.server = new Server();
			
			int maxIdleTime = this.portInfo.getAttributeToInt(EnvKey.Listener.MAX_IDLE, 30000);
			int headerSize = this.portInfo.getAttributeToInt(EnvKey.Listener.HEADER_SIZE, 1024 * 8);
			
			if(http != 0)
			{
				SelectChannelConnector connector = new SelectChannelConnector();
				connector.setPort(http);
				connector.setMaxIdleTime(maxIdleTime);
				connector.setRequestHeaderSize(headerSize);
				this.server.addConnector(connector);
			}
			
			if(https != 0)
			{
				String sslCtxClass = this.portInfo.getAttribute(EnvKey.Listener.SSL_CONTEXT);
				if(sslCtxClass == null || sslCtxClass.length() == 0)
				{
					throw new Exception("Https must be declared sslContextClass.");
				}
				
				SslContextFactoryBuilder sslCtxFactoryBuilder = (SslContextFactoryBuilder)Class.forName(sslCtxClass).newInstance();
				SslContextFactory sslCtxFactory = sslCtxFactoryBuilder.build();
				
				SslSelectChannelConnector sslConnector = new SslSelectChannelConnector(sslCtxFactory);
				sslConnector.setPort(https);
				sslConnector.setMaxIdleTime(maxIdleTime);
				sslConnector.setRequestHeaderSize(headerSize);
				this.server.addConnector(sslConnector);
			}
			
			String descriptor = this.portInfo.getAttribute(EnvKey.Listener.DESCRIPTOR);
			String resourceBase = this.portInfo.getAttribute(EnvKey.Listener.RESOURCE_BASE);
			String contextPath = this.portInfo.getAttribute(EnvKey.Listener.CONTEXT_PATH);
			
			WebAppContext webapp = new WebAppContext();
			webapp.setDescriptor(descriptor);
			webapp.setResourceBase(resourceBase);
			webapp.setContextPath(contextPath);
			
			this.server.setHandler(webapp);
			this.server.start();
			
			portList.remove(this.portInfo);
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
