package gate.adapter.registrar;

import gate.adapter.collector.*;

import java.util.*;
import java.util.concurrent.*;

public class CoreCollectorRegistrar implements CollectorRegistrar
{
	@Override
	public Map<Class<?>, Collector> entries()
	{
		Map<Class<?>, Collector> registry = new HashMap<>();

		registry.put(Collection.class, new CollectionCollector());
		registry.put(List.class, new ListCollector());
		registry.put(ArrayList.class, new ArrayListCollector());
		registry.put(LinkedList.class, new LinkedListCollector());
		registry.put(Set.class, new SetCollector());
		registry.put(LinkedHashSet.class, new LinkedHashSetCollector());
		registry.put(HashSet.class, new HashSetCollector());
		registry.put(SortedSet.class, new SortedSetCollector());
		registry.put(NavigableSet.class, new NavigableSetCollector());
		registry.put(TreeSet.class, new TreeSetCollector());
		registry.put(Queue.class, new QueueCollector());
		registry.put(Deque.class, new DequeCollector());
		registry.put(ArrayDeque.class, new ArrayDequeCollector());
		registry.put(BlockingQueue.class, new BlockingQueueCollector());
		registry.put(LinkedBlockingQueue.class, new LinkedBlockingQueueCollector());
		registry.put(BlockingDeque.class, new BlockingDequeCollector());
		registry.put(LinkedBlockingDeque.class, new LinkedBlockingDequeCollector());
		registry.put(CopyOnWriteArrayList.class, new CopyOnWriteArrayListCollector());
		registry.put(CopyOnWriteArraySet.class, new CopyOnWriteArraySetCollector());
		registry.put(EnumSet.class, new EnumSetCollector());

		return registry;
	}
}
