package gate.producer;

import gate.annotation.DataSource;
import gate.annotation.DefaultDataSource;
import gate.sql.LinkSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class LinkSourceProducer
{

	private static final Map<String, LinkSource> LINK_SOURCES = new ConcurrentHashMap<>();

	@Produces
	@DefaultDataSource
	public LinkSource defaultLinkSource(InjectionPoint ip)
	{
		return LinkSource.of();

	}

	@Produces
	@DataSource("")
	public LinkSource namedLinkSource(InjectionPoint ip)
	{
		String name = ip.getAnnotated().getAnnotation(DataSource.class).value();
		return LINK_SOURCES.computeIfAbsent(name, LinkSource::of);
	}
}
