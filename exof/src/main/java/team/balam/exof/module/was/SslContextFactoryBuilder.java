package team.balam.exof.module.was;

import org.eclipse.jetty.util.ssl.SslContextFactory;

import team.balam.exof.module.listener.PortInfo;

public interface SslContextFactoryBuilder
{
	SslContextFactory build(PortInfo _portInfo) throws Exception;
}
