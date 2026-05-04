package gate.adapter.registry;

import gate.adapter.columnMapper.*;
import gate.adapter.registrar.ColumnMapperRegistrar;
import gate.annotation.Adapter;
import gate.annotation.Entity;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class ColumnMapperRegistry extends Registry<ColumnMapper>
{
	public static ColumnMapperRegistry INSTANCE = new ColumnMapperRegistry();

	ColumnMapperRegistry()
	{
		super(ServiceLoader.load(ColumnMapperRegistrar.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.map(ColumnMapperRegistrar::entries)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
	}

	@Override protected ColumnMapper extractor(Class<?> type)
	{
		try
		{
			if (type.isAnnotationPresent(gate.sql.annotation.ColumnMapper.class))
				return type.getAnnotation(gate.sql.annotation.ColumnMapper.class).value()
						.getDeclaredConstructor().newInstance();

			return Adapter.Extractor.extract(type, ColumnMapper.class)
					.orElseGet(() ->
					{
						if (AdapterRegistry.INSTANCE.get(type)
								instanceof ColumnMapper columnMapper)
							return columnMapper;

						if (type.isAnnotationPresent(Entity.class))
							return new EntityColumnMapper();

						return null;
					});
		} catch (ReflectiveOperationException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override protected ColumnMapper fallback(Class<?> type)
	{
		for (var method : type.getDeclaredMethods())
			if ("valueOf".equals(method.getName())
			    && method.getParameterCount() == 1
			    && method.getParameterTypes()[0] == String.class
			    && Modifier.isStatic(method.getModifiers()))
				return new DefaultColumnMapper(method);

		return type.isRecord() ? new RecordColumnMapper() : new ObjectColumnMapper();
	}
}