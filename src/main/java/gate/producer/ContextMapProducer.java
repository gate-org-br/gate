package gate.producer;

import gate.annotation.Name;
import gate.util.JNDIContextMap;
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
	public <T> JNDIContextMap<T> produce(InjectionPoint injectionPoint)
	{
		return new JNDIContextMap(injectionPoint.getAnnotated().getAnnotation(Name.class).value());
	}
}
