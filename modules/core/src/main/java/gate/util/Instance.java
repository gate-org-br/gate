package gate.util;

import gate.adapter.collector.Collector;
import gate.adapter.mapCollector.MapCollector;
import gate.error.PropertyError;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

public final class Instance
{
	private Instance() {}

	public static Object create(Class<?> type)
	{
		if (Collection.class.isAssignableFrom(type))
			return Collector.fromArray(type, new Object[0]);
		if (Map.class.isAssignableFrom(type))
			return MapCollector.fromMap(type, Map.of());
		try
		{
			Constructor<?> constructor = Stream.of(type.getConstructors())
					.filter(e -> e.getParameterCount() == 0)
					.findAny()
					.orElse(null);
			if (constructor == null)
				throw new PropertyError("No default constructor found in %s.", type.getName());
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
		         | InvocationTargetException ex)
		{
			throw new PropertyError("Error trying to create a instance of %s.", type.getName());
		}
	}
}
