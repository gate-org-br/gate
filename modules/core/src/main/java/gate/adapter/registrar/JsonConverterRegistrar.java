package gate.adapter.registrar;

import gate.adapter.jsonConverter.JsonConverter;

import java.util.Map;

/**
 * Registers custom JSON converters for java types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 * To register a custom JSON converter, create an implementation of this interface
 * and declare it in {@code META-INF/services/gate.adapter.registrar.JsonConverterRegistrar}.
 */
public interface JsonConverterRegistrar extends Registrar<JsonConverter>
{
	/**
	 * Registers JSON converters into the provided registry map.
	 */
	Map<Class<?>, JsonConverter> entries();
}