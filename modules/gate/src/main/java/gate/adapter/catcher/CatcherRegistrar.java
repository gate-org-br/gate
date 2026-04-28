package gate.adapter.catcher;

import gate.adapter.registrar.Registrar;

import java.util.Map;

/**
 * Registers catchers for exception types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 * To register a custom catcher, create an implementation of this interface
 * and declare it in {@code META-INF/services/gate.catcher.CatcherRegistrar}.
 */
public interface CatcherRegistrar extends Registrar<Class<? extends Catcher>>
{

	/**
	 * Registers catchers into the provided registry map.
	 */
	Map<Class<?>, Class<? extends Catcher>> entries();
}