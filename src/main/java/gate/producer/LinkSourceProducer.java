package gate.producer;

import gate.annotation.DataSource;
import gate.annotation.DefaultDataSource;
import gate.sql.LinkSource;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class LinkSourceProducer
{

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
		return LinkSource.of(ip.getAnnotated().getAnnotation(DataSource.class).value());
	}
}
