package gate.adapter.registry;

import gate.adapter.jsonConverter.*;
import gate.adapter.registrar.JsonConverterRegistrar;
import gate.annotation.Entity;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class JsonConverterRegistry extends Registry<JsonConverter>
{
	public static JsonConverterRegistry INSTANCE = new JsonConverterRegistry();

	JsonConverterRegistry()
	{
		super(ServiceLoader.load(JsonConverterRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(JsonConverterRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override
	protected JsonConverter extractor(Class<?> type)
	{
		try
		{
			if (type.isAnnotationPresent(gate.annotation.JsonConverter.class))
				return type.getAnnotation(gate.annotation.JsonConverter.class)
						.value()
						.getDeclaredConstructor()
						.newInstance();
			if (AdapterRegistry.INSTANCE.get(type) instanceof JsonConverter jsonConverter)
				return jsonConverter;
			return null;
		} catch (ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	protected JsonConverter fallback(Class<?> type)
	{
		if (type.isArray())
			return new ArrayJsonConverter();

		if (type.isAnnotationPresent(Entity.class))
			return new EntityJsonConverter();

		for (var method : type.getDeclaredMethods())
			if ("valueOf".equals(method.getName())
					&& method.getParameterCount() == 1
					&& method.getParameterTypes()[0] == String.class
					&& Modifier.isStatic(method.getModifiers()))
				return new DefaultJsonConverter(method);

		return new ObjectJsonConverter();
	}
}
