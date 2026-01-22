package gate.cache;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

class TimeoutCache<T> implements Cache<T>
{

	private final Duration timeout;
	private final Supplier<T> supplier;
	private AtomicReference<CacheEntry<T>> value = new AtomicReference<>();

	TimeoutCache(Duration timeout, Supplier<T> supplier)
	{
		this.timeout = timeout;
		this.supplier = supplier;
	}

	@Override
	public T get()
	{
		return value.updateAndGet(e -> e != null && System.currentTimeMillis() - e.getTimestamp() < timeout.toMillis() ? e : new CacheEntry<T>(supplier.get())).getValue();
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
