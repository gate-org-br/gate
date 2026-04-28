package gate.adapter;

import gate.adapter.registrar.AdapterRegistrar;
import gate.adapter.registry.Registry;
import gate.annotation.Adapter;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class AdapterRegistry extends Registry<Object>
{
	public static final AdapterRegistry INSTANCE = new AdapterRegistry();

	private AdapterRegistry()
	{
		super(ServiceLoader.load(AdapterRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(AdapterRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override protected Object extractor(Class<?> type)
	{
		return Adapter.Extractor.extract(type).orElse(null);
	}

	@Override protected Object fallback(Class<?> type)
	{
		return null;
	}
}