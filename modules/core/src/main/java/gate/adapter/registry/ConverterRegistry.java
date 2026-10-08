package gate.adapter.registry;

import gate.adapter.converter.*;
import gate.adapter.registrar.ConverterRegistrar;
import gate.annotation.Entity;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class ConverterRegistry extends Registry<Converter>
{
	public static ConverterRegistry INSTANCE = new ConverterRegistry();

	ConverterRegistry()
	{
		super(ServiceLoader.load(ConverterRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(ConverterRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override
	protected Converter extractor(Class<?> type)
	{
		try
		{
			if (type.isAnnotationPresent(gate.annotation.Converter.class))
				return type.getAnnotation(gate.annotation.Converter.class).value()
						.getDeclaredConstructor().newInstance();
			if (AdapterRegistry.INSTANCE.get(type) instanceof Converter converter)
				return converter;
			return null;
		} catch (ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Converter fallback(Class<?> type)
	{
		if (type.isAnnotationPresent(Entity.class))
			return new EntityConverter();
		
		for (var method : type.getDeclaredMethods())
			if ("valueOf".equals(method.getName())
					&& method.getParameterCount() == 1
					&& method.getParameterTypes()[0] == String.class
					&& Modifier.isStatic(method.getModifiers()))
				return new DefaultConverter(method);

		return type.isRecord() ? new RecordConverter() : new ObjectConverter();
	}
}
