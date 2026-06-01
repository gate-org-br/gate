package gate.adapter.registrar;


import gate.adapter.jsonRenderer.JsonRenderer;

import java.util.Map;

/**
 * Registers custom JSON renderers for java types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 * To register a custom JSON renderer, create an implementation of this interface
 * and declare it in {@code META-INF/services/gate.adapter.registrar.JsonrendererRegistrar}.
 */
public interface JsonRendererRegistrar extends Registrar<JsonRenderer>
{
	/**
	 * Registers JSON renderers into the provided registry map.
	 */
	Map<Class<?>, JsonRenderer> entries();
}