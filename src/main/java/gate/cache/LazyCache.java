package gate.cache;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

class LazyCache<T> implements Cache<T>
{

	private final Supplier<T> supplier;
	private final AtomicReference<T> value = new AtomicReference<>();

	LazyCache(Supplier<T> supplier)
	{
		this.supplier = supplier;
	}

	@Override
	public T get()
	{
		return value.updateAndGet(e -> e != null ? e : supplier.get());
	}

}
