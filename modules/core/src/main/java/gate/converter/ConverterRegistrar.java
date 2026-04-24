package gate.converter;

import java.util.Map;

/**
 * Registers custom converters for java types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 * To register a custom converter, create an implementation of this interface
 * and declare it in {@code META-INF/services/gate.converter.ConverterRegistrar}.
 */
public interface ConverterRegistrar
{

	/**
	 * Registers converters into the provided registry map.
	 *
	 * @param registry map of java types to their associated converters
	 */
	void register(Map<Class<?>, Converter> registry);
}
