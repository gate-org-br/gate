package gate.cache;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

class PredicateTimeoutCache<T> implements Cache<T>
{

	private final Duration timeout;
	private final Supplier<T> supplier;
	private final Predicate<T> predicate;
	private final AtomicReference<CacheEntry<T>> value = new AtomicReference<>();

	PredicateTimeoutCache(Duration timeout, Predicate<T> predicate, Supplier<T> supplier)
	{
		this.timeout = timeout;
		this.predicate = predicate;
		this.supplier = supplier;
	}

	@Override
	public T get()
	{
		return value.updateAndGet(e -> e != null && System.currentTimeMillis() - e.getTimestamp() < timeout.toMillis()
			&& predicate.test(e.getValue()) ? e : new CacheEntry<T>(supplier.get())).getValue();
	}

	@Override
	public void invalidate()
	{
		value.set(null);
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
