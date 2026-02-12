package gate.cache;

import java.util.function.Predicate;

class LazyCache<T> implements Cache<T>
{

	private final Generator<T> generator;
	private final Predicate<Entry<T>> predicate;
	private final long ttl;
	private final long retry;
	private volatile Entry<T> entry;

	LazyCache(Generator<T> generator, Predicate<Entry<T>> predicate, long ttl, long retry)
	{
		this.generator = generator;
		this.predicate = predicate;
		this.ttl = ttl;
		this.retry = retry;
	}

	@Override
	public T get()
	{
		Entry<T> local = entry;
		if (local != null && predicate.test(local))
			return local.value();
		synchronized (this)
		{
			local = entry;
			if (local != null && predicate.test(local))
				return local.value();

			long now = System.nanoTime();
			try
			{
				T v = generator.get();
				entry = new Entry<>(v, now + ttl, -1);
				return v;
			} catch (Exception ex)
			{
				if (local != null && retry > 0)
				{
					entry = new Entry<>(local.value(), local.ttl(), now + retry);
					return local.value();
				}

				throw ex instanceof RuntimeException runtimeException ? runtimeException
						: new RuntimeException(ex);
			}
		}
	}

	@Override
	public void invalidate()
	{
		synchronized (this)
		{
			entry = null;
		}
	}
}
