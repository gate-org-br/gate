package gate.adapter.registry;

import gate.adapter.collector.Collector;
import gate.adapter.registrar.CollectorRegistrar;
import gate.error.AppError;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class CollectorRegistry extends Registry<Collector>
{
	public static CollectorRegistry INSTANCE = new CollectorRegistry();

	CollectorRegistry()
	{
		super(ServiceLoader.load(CollectorRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(CollectorRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override
	protected Collector extractor(Class<?> type)
	{
		try
		{
			return type.isAnnotationPresent(gate.annotation.Collector.class)
					? type.getAnnotation(gate.annotation.Collector.class).value()
					.getDeclaredConstructor().newInstance()
					: AdapterRegistry.INSTANCE.get(type) instanceof Collector collector
					  ? collector
					  : null;
		} catch (ReflectiveOperationException ex)
		{
			throw new AppError(ex);
		}
	}

	@Override
	protected Collector fallback(Class<?> type)
	{
		throw new IllegalStateException("No collector found for %s.".formatted(type.getName()));
	}
}
