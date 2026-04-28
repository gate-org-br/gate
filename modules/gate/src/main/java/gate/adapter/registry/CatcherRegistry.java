package gate.adapter.registry;

import gate.adapter.catcher.Catcher;
import gate.adapter.catcher.CatcherRegistrar;
import gate.adapter.catcher.ThrowableCatcher;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class CatcherRegistry extends Registry<Class<? extends Catcher>>
{
	public static final CatcherRegistry INSTANCE = new CatcherRegistry();

	public CatcherRegistry()
	{
		super(ServiceLoader.load(CatcherRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(CatcherRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override protected Class<? extends Catcher> extractor(Class<?> type)
	{
		return type.isAnnotationPresent(gate.annotation.Catcher.class)
				? type.getAnnotation(gate.annotation.Catcher.class).value()
				: null;
	}

	@Override protected Class<? extends Catcher> fallback(Class<?> type)
	{
		return ThrowableCatcher.class;
	}
}