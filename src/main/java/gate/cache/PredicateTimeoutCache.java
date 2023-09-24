package gate.cache;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

class PredicateTimeoutCache<T> implements Cache<T>
{

	private final long timeout;
	private final Supplier<T> supplier;
	private final Predicate<T> predicate;
	private AtomicReference<CacheEntry<T>> value;

	PredicateTimeoutCache(long timeout, Predicate<T> predicate, Supplier<T> supplier)
	{
		this.timeout = timeout;
		this.predicate = predicate;
		this.supplier = supplier;
	}

	@Override
	public T get()
	{
		return value.updateAndGet(e -> e != null && System.currentTimeMillis() - e.getTimestamp() < timeout && !predicate.test(e.getValue()) ? e : new CacheEntry<T>(supplier.get())).getValue();
	}

	private static class CacheEntry<T>
	{

		private final T value;
		private final long timestamp;

		private CacheEntry(T value)
		{
			this.value = value;
			this.timestamp = System.currentTimeMillis();
		}

		T getValue()
		{
			return value;
		}

		long getTimestamp()
		{
			return timestamp;
		}
	}

}
