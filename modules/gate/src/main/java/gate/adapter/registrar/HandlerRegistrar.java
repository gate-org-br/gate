package gate.adapter.registrar;

import gate.adapter.handler.Handler;

import java.util.Map;

/**
 * Registers custom handlers for java types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 * To register a custom handler, create an implementation of this interface
 * and declare it in {@code META-INF/services/gate.adapter.registrar.HandlerRegistrar}.
 */
public interface HandlerRegistrar extends Registrar<Class<? extends Handler>>
{

	/**
	 * Gets the handlers registered by this registrar.
	 *
	 * @return map of java types to their associated handler classes
	 */
	Map<Class<?>, Class<? extends Handler>> entries();
}