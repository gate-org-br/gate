package gate.adapter.registry;

import gate.adapter.mapCollector.MapCollector;
import gate.adapter.registrar.MapCollectorRegistrar;
import gate.error.AppError;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class MapCollectorRegistry extends Registry<MapCollector>
{
	public static MapCollectorRegistry INSTANCE = new MapCollectorRegistry();

	MapCollectorRegistry()
	{
		super(ServiceLoader.load(MapCollectorRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(MapCollectorRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override
	protected MapCollector extractor(Class<?> type)
	{
		try
		{
			return type.isAnnotationPresent(gate.annotation.MapCollector.class)
					? type.getAnnotation(gate.annotation.MapCollector.class).value()
					.getDeclaredConstructor().newInstance()
					: AdapterRegistry.INSTANCE.get(type) instanceof MapCollector collector
					  ? collector
					  : null;
		} catch (ReflectiveOperationException ex)
		{
			throw new AppError(ex);
		}
	}

	@Override
	protected MapCollector fallback(Class<?> type)
	{
		throw new IllegalStateException("No collector found for %s.".formatted(type.getName()));
	}
}
