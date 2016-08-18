package team.balam.exof.module.was;

import org.eclipse.jetty.util.ssl.SslContextFactory;

public interface SslContextFactoryBuilder
{
	SslContextFactory build() throws Exception;
}
