package gate.handler;

import gate.registrar.Registrar;

import java.util.Map;

/**
 * Registers custom handlers for java types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 * To register a custom handler, create an implementation of this interface
 * and declare it in {@code META-INF/services/gate.handler.HandlerRegistrar}.
 */
public interface HandlerRegistrar extends Registrar<Class<? extends Handler>>
{

	/**
	 * Registers handlers into the provided registry map.
	 *
	 * @param registry map of java types to their associated handler classes
	 */
	void register(Map<Class<?>, Class<? extends Handler>> registry);
}
