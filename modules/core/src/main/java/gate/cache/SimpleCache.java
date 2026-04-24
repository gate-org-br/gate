package gate.cache;

public class SimpleCache<T> implements Cache<T>
{

	private volatile T entry;
	private final Generator<T> generator;

	SimpleCache(Generator<T> generator)
	{
		this.generator = generator;
	}

	@Override
	public T get()
	{
		var local = entry;
		if (local != null)
			return local;

		synchronized (this)
		{
			local = entry;
			if (local != null)
				return local;

			try
			{
				return entry = generator.get();
			} catch (Exception ex)
			{
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
			this.entry = null;
		}
	}

}
