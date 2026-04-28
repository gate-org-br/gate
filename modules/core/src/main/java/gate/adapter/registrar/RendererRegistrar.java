package gate.adapter.registrar;

import gate.adapter.renderer.Renderer;

import java.util.Map;

/**
 * Registers custom renderers for java types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 * To register a custom renderer, create an implementation of this interface
 * and declare it in {@code META-INF/services/gate.adapter.registrar.RendererRegistrar}.
 */
public interface RendererRegistrar extends Registrar<Renderer>
{
	/**
	 * Registers converters into the provided registry map.
	 */
	Map<Class<?>, Renderer> entries();
}