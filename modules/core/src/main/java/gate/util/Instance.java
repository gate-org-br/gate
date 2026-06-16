package gate.util;

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
			return createCollection(type);
		if (Map.class.isAssignableFrom(type))
			return createMap(type);
		return createDefault(type);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Collection<?>> T createCollection(Class<?> type)
	{
		if (type == Collection.class || type == List.class || type == ArrayList.class)
			return (T) new ArrayList<>();
		if (type == LinkedList.class)
			return (T) new LinkedList<>();
		if (type == Set.class || type == LinkedHashSet.class)
			return (T) new LinkedHashSet<>();
		if (type == HashSet.class)
			return (T) new HashSet<>();
		if (type == SortedSet.class || type == NavigableSet.class || type == TreeSet.class)
			return (T) new TreeSet<>();
		if (type == Queue.class)
			return (T) new LinkedList<>();
		if (type == Deque.class || type == ArrayDeque.class)
			return (T) new ArrayDeque<>();
		if (type == java.util.concurrent.BlockingQueue.class
				|| type == java.util.concurrent.LinkedBlockingQueue.class)
			return (T) new java.util.concurrent.LinkedBlockingQueue<>();
		if (type == java.util.concurrent.BlockingDeque.class
				|| type == java.util.concurrent.LinkedBlockingDeque.class)
			return (T) new java.util.concurrent.LinkedBlockingDeque<>();
		if (type == java.util.concurrent.CopyOnWriteArrayList.class)
			return (T) new java.util.concurrent.CopyOnWriteArrayList<>();
		if (type == java.util.concurrent.CopyOnWriteArraySet.class)
			return (T) new java.util.concurrent.CopyOnWriteArraySet<>();

		if (!Collection.class.isAssignableFrom(type))
			throw new PropertyError("%s is not a collection type.", type.getName());
		return (T) createDefault(type);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Map<?, ?>> T createMap(Class<?> type)
	{
		if (type == Map.class || type == LinkedHashMap.class)
			return (T) new LinkedHashMap<>();
		if (type == HashMap.class)
			return (T) new HashMap<>();
		if (type == SortedMap.class || type == NavigableMap.class || type == TreeMap.class)
			return (T) new TreeMap<>();
		if (type == java.util.concurrent.ConcurrentMap.class
				|| type == java.util.concurrent.ConcurrentHashMap.class)
			return (T) new java.util.concurrent.ConcurrentHashMap<>();
		if (type == java.util.concurrent.ConcurrentNavigableMap.class
				|| type == java.util.concurrent.ConcurrentSkipListMap.class)
			return (T) new java.util.concurrent.ConcurrentSkipListMap<>();
		if (type == Hashtable.class)
			return (T) new Hashtable<>();
		if (type == Properties.class)
			return (T) new Properties();

		if (!Map.class.isAssignableFrom(type))
			throw new PropertyError("%s is not a map type.", type.getName());
		return (T) createDefault(type);
	}

	public static Object createDefault(Class<?> type)
	{
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
