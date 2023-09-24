package gate.cache;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

class TimeoutCache<T> implements Cache<T>
{

	private final long timeout;
	private final Supplier<T> supplier;
	private AtomicReference<CacheEntry<T>> value;

	TimeoutCache(long timeout, Supplier<T> supplier)
	{
		this.timeout = timeout;
		this.supplier = supplier;
	}

	@Override
	public T get()
	{
		return value.updateAndGet(e -> e != null && System.currentTimeMillis() - e.getTimestamp() < timeout ? e : new CacheEntry<T>(supplier.get())).getValue();
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
