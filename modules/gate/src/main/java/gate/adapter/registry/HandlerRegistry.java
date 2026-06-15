package gate.adapter.registry;

import gate.adapter.handler.EntityHandler;
import gate.adapter.handler.Handler;
import gate.adapter.handler.JsonTextHandler;
import gate.adapter.registrar.HandlerRegistrar;
import gate.annotation.Entity;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class HandlerRegistry extends Registry<Class<? extends Handler>>
{
	public static final HandlerRegistry INSTANCE = new HandlerRegistry();

	public HandlerRegistry()
	{
		super(ServiceLoader.load(HandlerRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(HandlerRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override
	protected Class<? extends Handler> extractor(Class<?> type)
	{
		if (type.isAnnotationPresent(gate.annotation.Handler.class))
			return type.getAnnotation(gate.annotation.Handler.class).value();
		if (AdapterRegistry.INSTANCE.get(type) instanceof Handler handler)
			return handler.getClass();
		return null;
	}

	@Override
	protected Class<? extends Handler> fallback(Class<?> type)
	{
		if (type.isAnnotationPresent(Entity.class))
			return EntityHandler.class;
		return JsonTextHandler.class;
	}
}
