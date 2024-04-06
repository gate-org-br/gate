package gate.producer;

import gate.annotation.Name;
import gate.sql.Link;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

/**
 *
 * @author davins
 *
 * Produces and disposes Connection objects using specified data sources.
 *
 */
public class ConnectionProducer
{

	@Name("")
	@Produces
	public Link produce(InjectionPoint injectionPoint)
	{
		return new Link(injectionPoint.getAnnotated().getAnnotation(Name.class).value());
	}

	public void dispose(@Disposes @Name("") Link link)
	{
		link.close();
	}

}
