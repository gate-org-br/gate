package gate.producer;

import gate.annotation.Name;
import gate.sql.Link;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

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
