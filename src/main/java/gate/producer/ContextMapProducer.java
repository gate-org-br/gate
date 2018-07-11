package gate.producer;

import gate.annotation.Name;
import gate.util.ContextMap;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 *
 * @author davins
 *
 * Produces and disposes Connection objects using specified data sources.
 *
 */
public class ContextMapProducer
{

	@Name("")
	@Produces
	public <T> ContextMap<T> produce(InjectionPoint injectionPoint)
	{
		return new ContextMap(injectionPoint.getAnnotated().getAnnotation(Name.class).value());
	}
}
