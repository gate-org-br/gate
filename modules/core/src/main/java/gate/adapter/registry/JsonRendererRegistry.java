package gate.adapter.registry;

import gate.adapter.jsonRenderer.*;
import gate.adapter.registrar.JsonRendererRegistrar;
import gate.error.AppError;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class JsonRendererRegistry extends Registry<JsonRenderer>
{
	public static JsonRendererRegistry INSTANCE = new JsonRendererRegistry();

	JsonRendererRegistry()
	{
		super(ServiceLoader.load(JsonRendererRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(JsonRendererRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override protected JsonRenderer extractor(Class<?> type)
	{
		if (type.isAnnotationPresent(gate.annotation.JsonRenderer.class))
			try
			{
				return type.getAnnotation(gate.annotation.JsonRenderer.class)
						.value()
						.getDeclaredConstructor()
						.newInstance();
			} catch (ReflectiveOperationException ex)
			{
				throw new AppError(ex);
			}

		return null;
	}

	@Override protected JsonRenderer fallback(Class<?> type)
	{
		if (type.isArray())
			return new ArrayJsonRenderer();
		if (type.isRecord())
			return new RecordJsonRenderer();

		for (var method : type.getDeclaredMethods())
			if ("valueOf".equals(method.getName())
			    && method.getParameterCount() == 1
			    && method.getParameterTypes()[0] == String.class
			    && Modifier.isStatic(method.getModifiers()))
				return new DefaultJsonRenderer();

		return new ObjectJsonRenderer();
	}
}