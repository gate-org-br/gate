package gate.producer;

import gate.annotation.Type;
import gate.base.CrudControl;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import java.io.Serializable;

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
