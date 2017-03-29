package team.balam.exof.module.was;

import org.eclipse.jetty.util.ssl.SslContextFactory;

import team.balam.exof.module.listener.PortInfo;

public class SimpleSslContextFactory implements SslContextFactoryBuilder
{
	@Override
	public SslContextFactory build(PortInfo _portInfo) throws Exception
	{
		SslContextFactory factory = new SslContextFactory();
		factory.setKeyStorePath(_portInfo.getAttribute("keystorePath"));
		factory.setKeyStorePassword(_portInfo.getAttribute("keystorePassword"));
		
		return factory;
	}
}
