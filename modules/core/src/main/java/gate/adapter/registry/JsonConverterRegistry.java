package gate.adapter.registry;

import gate.adapter.jsonConverter.JsonConverter;
import gate.adapter.jsonConverter.ArrayJsonConverter;
import gate.adapter.jsonConverter.DefaultJsonConverter;
import gate.adapter.jsonConverter.ObjectJsonConverter;
import gate.adapter.registrar.JsonConverterRegistrar;
import gate.error.AppError;

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

	@Override protected JsonConverter extractor(Class<?> type)
	{
		if (type.isAnnotationPresent(gate.annotation.JsonConverter.class))
			try
			{
				return type.getAnnotation(gate.annotation.JsonConverter.class)
						.value()
						.getDeclaredConstructor()
						.newInstance();
			} catch (ReflectiveOperationException ex)
			{
				throw new AppError(ex);
			}

		return null;
	}

	@Override protected JsonConverter fallback(Class<?> type)
	{
		if (type.isArray())
			return new ArrayJsonConverter();

		for (var method : type.getDeclaredMethods())
			if ("valueOf".equals(method.getName())
			    && method.getParameterCount() == 1
			    && method.getParameterTypes()[0] == String.class
			    && Modifier.isStatic(method.getModifiers()))
				return new DefaultJsonConverter(method);

		return new ObjectJsonConverter();
	}
}
