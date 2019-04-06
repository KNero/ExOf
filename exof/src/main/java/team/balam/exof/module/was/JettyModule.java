package team.balam.exof.module.was;

import io.netty.util.internal.StringUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import team.balam.exof.ExternalClassLoader;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.Module;

import javax.servlet.DispatcherType;
import java.io.File;
import java.util.EnumSet;

public class JettyModule implements Module {
	private PortInfo portInfo;
	private Server server;
	
	public void setPortInfo(PortInfo portInfo) {
		this.portInfo = portInfo;
	}

	@Override
	public void start() throws Exception {
		if (!this.portInfo.isNull()) {
			int http = this.portInfo.getAttributeToInt(EnvKey.Listener.HTTP, 0);
			int https = this.portInfo.getAttributeToInt(EnvKey.Listener.HTTPS, 0);

			if (http == 0 && https == 0) {
				throw new Exception("Http port and https port is empty.");
			}

			this.server = new Server();

			int maxIdleTime = this.portInfo.getAttributeToInt(EnvKey.Listener.MAX_IDLE, 30000);
			int headerSize = this.portInfo.getAttributeToInt(EnvKey.Listener.HEADER_SIZE, 1024 * 8);

			if (http != 0) {
				this.addHttp(http, maxIdleTime, headerSize);
			}

			if (https != 0) {
				this.addHttps(https, maxIdleTime, headerSize);
			}

			WebAppContext webapp = new WebAppContext();
			webapp.setDescriptor(this.portInfo.getAttribute(EnvKey.Listener.DESCRIPTOR));
			webapp.setResourceBase(this.portInfo.getAttribute(EnvKey.Listener.RESOURCE_BASE));
			webapp.setContextPath(this.portInfo.getAttribute(EnvKey.Listener.CONTEXT_PATH));

			addFilter(webapp);

			this.server.setHandler(webapp);
			this.server.start();
		}
	}

	private void addFilter(WebAppContext webapp) throws Exception {
		String tempDir = portInfo.getAttribute(EnvKey.Listener.TEMP_DIRECTORY);
		if (StringUtil.isNullOrEmpty(tempDir)) {
			throw new Exception("jetty port " + EnvKey.Listener.TEMP_DIRECTORY + " is empty.");
		}

		MultiPartFilter.deleteFiles = true;
		MultiPartFilter.tempdir = new File(tempDir);
		webapp.addFilter(MultiPartFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

		EnumSet<DispatcherType> dispatcherTypes =
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
		webapp.addFilter(ExofRequestFilter.class, "/*", dispatcherTypes);
	}

	private void addHttp(int port, int maxIdleTime, int headerSize) {
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		connector.setMaxIdleTime(maxIdleTime);
		connector.setRequestHeaderSize(headerSize);
		this.server.addConnector(connector);
	}

	private void addHttps(int port, int maxIdleTime, int headerSize) throws Exception {
		String sslCtxClass = this.portInfo.getAttribute(EnvKey.Listener.SSL_CONTEXT);
		if (sslCtxClass == null || sslCtxClass.length() == 0) {
			throw new Exception("Https must be declared sslContextClass.");
		}

		SslContextFactoryBuilder sslCtxFactoryBuilder = (SslContextFactoryBuilder) ExternalClassLoader.loadClass(sslCtxClass).newInstance();
		SslContextFactory sslCtxFactory = sslCtxFactoryBuilder.build(this.portInfo);

		SslSelectChannelConnector sslConnector = new SslSelectChannelConnector(sslCtxFactory);
		sslConnector.setPort(port);
		sslConnector.setMaxIdleTime(maxIdleTime);
		sslConnector.setRequestHeaderSize(headerSize);
		this.server.addConnector(sslConnector);
	}

	@Override
	public void stop() throws Exception {
		if (this.server != null) {
			this.server.stop();
		}
	}
}
