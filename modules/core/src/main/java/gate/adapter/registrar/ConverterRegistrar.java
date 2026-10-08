package gate.adapter.registrar;

import gate.adapter.converter.Converter;

import java.util.Map;

/**
 * Registers custom converters for java types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 * To register a custom converter, create an implementation of this interface
 * and declare it in {@code META-INF/services/gate.adapter.registrar.ConverterRegistrar}.
 */
public interface ConverterRegistrar extends Registrar<Converter>
{
	/**
	 * Registers converters into the provided registry map.
	 */
	Map<Class<?>, Converter> entries();
}