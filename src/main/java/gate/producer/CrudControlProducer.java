package gate.producer;

import gate.annotation.Type;
import gate.base.CrudControl;
import java.io.Serializable;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * @author davins
 *
 * Produces CrudControl objects for the specified type
 */
public class CrudControlProducer implements Serializable
{

	@Produces
	@Type(Class.class)
	public <T> CrudControl<T> produce(InjectionPoint injectionPoint)
	{
		return new CrudControl<>((Class<T>) injectionPoint.getAnnotated().getAnnotation(Type.class).value());
	}
}
