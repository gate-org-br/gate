package gate.adapter.registry;

import gate.adapter.AdapterRegistry;
import gate.adapter.metadata.Metadata;
import gate.adapter.registrar.MetadataRegistrar;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class MetadataRegistry extends Registry<Metadata>
{
	public static MetadataRegistry INSTANCE = new MetadataRegistry();

	MetadataRegistry()
	{
		super(ServiceLoader.load(MetadataRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(MetadataRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override protected Metadata extractor(Class<?> type)
	{
		var metadata = gate.annotation.Metadata.Extractor.extract(type);
		if (metadata != Metadata.EMPTY)
			return metadata;

		if (AdapterRegistry.INSTANCE.get(type) instanceof Metadata adapter)
			return adapter;

		return null;
	}

	@Override protected Metadata fallback(Class<?> type)
	{
		return Metadata.EMPTY;
	}
}