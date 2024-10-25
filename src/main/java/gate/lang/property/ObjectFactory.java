package gate.lang.property;

import java.util.*;
import java.util.concurrent.*;

public class ObjectFactory
{
	public static Object create(Class<?> type) throws ReflectiveOperationException
	{
		if (type == List.class)
			return new ArrayList<>();
		if (type == Set.class)
			return new HashSet<>();
		if (type == Map.class)
			return new HashMap<>();
		if (type == ConcurrentMap.class)
			return new ConcurrentHashMap<>();
		if (type == Queue.class)
			return new LinkedList<>();
		if (type == Deque.class)
			return new ArrayDeque<>();
		if (type == SortedSet.class)
			return new TreeSet<>();
		if (type == NavigableSet.class)
			return new TreeSet<>();
		if (type == SortedMap.class)
			return new TreeMap<>();
		if (type == NavigableMap.class)
			return new TreeMap<>();
		if (type == BlockingQueue.class)
			return new LinkedBlockingQueue<>();
		if (type == BlockingDeque.class)
			return new LinkedBlockingDeque<>();

		return type.getDeclaredConstructor().newInstance();
	}
}
