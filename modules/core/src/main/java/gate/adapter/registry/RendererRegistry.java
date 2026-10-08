package gate.adapter.registry;

import gate.adapter.registrar.RendererRegistrar;
import gate.adapter.renderer.EntityRenderer;
import gate.adapter.renderer.ObjectRenderer;
import gate.adapter.renderer.RecordRenderer;
import gate.adapter.renderer.Renderer;
import gate.annotation.Entity;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class RendererRegistry extends Registry<Renderer>
{
	public static RendererRegistry INSTANCE = new RendererRegistry();

	RendererRegistry()
	{
		super(ServiceLoader.load(RendererRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(RendererRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override
	protected Renderer extractor(Class<?> type)
	{
		try
		{
			if (type.isAnnotationPresent(gate.annotation.Renderer.class))
				return type.getAnnotation(gate.annotation.Renderer.class).value()
						.getDeclaredConstructor().newInstance();
			if (AdapterRegistry.INSTANCE.get(type) instanceof Renderer Renderer)
				return Renderer;
			return null;
		} catch (ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Renderer fallback(Class<?> type)
	{
		if (type.isAnnotationPresent(Entity.class))
			return new EntityRenderer();
		if (type.isRecord())
			return new RecordRenderer();
		return new ObjectRenderer();
	}
}
