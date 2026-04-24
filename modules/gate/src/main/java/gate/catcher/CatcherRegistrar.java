package gate.catcher;

import java.util.Map;

/**
 * Registers catchers for exception types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 * To register a custom catcher, create an implementation of this interface
 * and declare it in {@code META-INF/services/gate.catcher.CatcherRegistrar}.
 */
public interface CatcherRegistrar
{

	/**
	 * Registers catchers into the provided registry map.
	 *
	 * @param registry map of exception types to their associated catcher classes
	 */
	void register(Map<Class<?>, Class<? extends Catcher>> registry);
}
