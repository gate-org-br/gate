package gate.cache;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Cache<T>
{

	/**
	 * Retrieves a cached value.
	 *
	 * @return The cached value.
	 */
	public T get();

	/**
	 * Creates a cache for a lazily supplied value.
	 *
	 * @param <E> the value java type.
	 * @param supplier The supplier function to obtain the cached value.
	 * @return A Cache instance.
	 */
	public static <E> Cache<E> of(Supplier<E> supplier)
	{
		return new LazyCache<>(supplier);
	}

	/**
	 * Creates a cache for a lazily supplied value that expires after a timeout.
	 *
	 * @param <E> the value java type.
	 * @param supplier The supplier function to obtain the cached value.
	 * @param timeout The cache timeout duration in milliseconds.
	 * @return A Cache instance.
	 */
	public static <E> Cache<E> of(long timeout, Supplier<E> supplier)
	{
		return new TimeoutCache<>(timeout, supplier);
	}

	/**
	 * Creates a cache for a lazily supplied value that expires if a predicate is not met.
	 *
	 * @param <E> the value java type.
	 * @param predicate The predicate to determine if the cached value is still valid.
	 * @param supplier The supplier function to obtain the cached value.
	 * @return A Cache instance.
	 */
	public static <E> Cache<E> of(Predicate<E> predicate, Supplier<E> supplier)
	{
		return new PredicateCache<>(predicate, supplier);
	}

	/**
	 * Creates a cache for a lazily supplied value that expires after a timeout or if a predicate is not met.
	 *
	 * @param <E> the value java type.
	 * @param timeout The cache timeout duration in milliseconds.
	 * @param predicate The predicate to determine if the cached value is still valid.
	 * @param supplier The supplier function to obtain the cached value.
	 * @return A Cache instance.
	 */
	public static <E> Cache<E> of(long timeout, Predicate<E> predicate, Supplier<E> supplier)
	{
		return new PredicateTimeoutCache<>(timeout, predicate, supplier);
	}

}
