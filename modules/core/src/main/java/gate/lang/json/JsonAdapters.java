package gate.lang.json;

import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

final class JsonAdapters
{

	static final JsonAdapters INSTANCE = new JsonAdapters();

	private final Map<Class<?>, JsonAdapter<?>> instances = new ConcurrentHashMap<>();

	private JsonAdapters()
	{
	}

	@SuppressWarnings("unchecked")
	<T> JsonAdapter<T> get(Class<T> type)
	{
		return (JsonAdapter<T>) instances.computeIfAbsent(type, e ->
		{
			try
			{
				for (Class<?> clazz = e; clazz != null; clazz = clazz.getSuperclass())
				{
					if (instances.containsKey(clazz))
						return instances.get(clazz);
					if (clazz.isAnnotationPresent(gate.annotation.JsonAdapter.class))
						return clazz.getAnnotation(gate.annotation.JsonAdapter.class)
								.value()
								.getDeclaredConstructor()
								.newInstance();
					if (Stream.of(clazz.getInterfaces())
								.filter(inter -> instances.containsKey(inter)
							                     || inter.isAnnotationPresent(gate.annotation.JsonAdapter.class))
								.count() == 1)
						for (Class<?> inter : clazz.getInterfaces())
							if (instances.containsKey(inter))
								return instances.get(inter);
							else if (inter.isAnnotationPresent(gate.annotation.JsonAdapter.class))
								return inter.getAnnotation(gate.annotation.JsonAdapter.class)
										.value()
										.getDeclaredConstructor()
										.newInstance();
				}
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
			         | InvocationTargetException ex)
			{
				LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
			}

			return null;
		});
	}

	<T> void register(Class<T> type, JsonAdapter<? super T> adapter)
	{
		instances.put(type, adapter);
	}
}