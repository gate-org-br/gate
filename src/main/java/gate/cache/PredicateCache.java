package gate.cache;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * @author davinunesdasilva
 */
class PredicateCache<T> implements Cache<T>
{

	private final Supplier<T> supplier;
	private final Predicate<T> predicate;
	private final AtomicReference<T> value = new AtomicReference<>();

	PredicateCache(Predicate<T> predicate, Supplier<T> supplier)
	{
		this.supplier = supplier;
		this.predicate = predicate;
	}

	@Override
	public T get()
	{
		return value.updateAndGet(e -> e != null && !predicate.test(e) ? e : supplier.get());
	}

}
