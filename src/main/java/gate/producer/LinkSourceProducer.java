package gate.producer;

import gate.annotation.DataSource;
import gate.annotation.DefaultDataSource;
import gate.sql.LinkSource;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

public class LinkSourceProducer
{

	@Produces
	@DefaultDataSource
	public LinkSource defaultLinkSource()
	{
		return LinkSource.of();

	}

	@Produces
	@DataSource("")
	public LinkSource namedLinkSource(InjectionPoint ip)
	{
		String name = ip.getAnnotated().getAnnotation(DataSource.class).value();
		return LinkSource.of(name);
	}
}
