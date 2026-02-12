package gate.cache;

import java.time.Duration;
import java.util.function.Predicate;

/**
 * Builder for creating lazy caches with TTL, custom validation and retry support.
 *
 * <p>
 * The builder allows configuring:
 * <ul>
 * <li><b>TTL</b>: time-to-live for cached entries
 * <li><b>Predicate</b>: custom validation on stored values
 * <li><b>Retry</b>: time to reuse stale values when generation fails
 * </ul>
 *
 * <p>
 * Usage example:
 * <pre>{@code
 * Cache<String> cache = Cache.builder(() -> fetchFromDatabase())
 *     .ttl(Duration.ofMinutes(5))
 *     .predicate(s -> s != null && !s.isEmpty())
 *     .retry(Duration.ofSeconds(30))
 *     .build();
 * }</pre>
 *
 * @param <T> type of the cached value
 */
public class Builder<T>
{

	private long retry = -1;
	private long ttl = Long.MAX_VALUE;
	private final Generator<T> generator;
	private Predicate<Entry<T>> predicate;

	Builder(Generator<T> generator)
	{
		this.generator = generator;
	}

	/**
	 * Sets the time-to-live (TTL) for cache entries.
	 *
	 * <p>
	 * After TTL expires, the entry will be regenerated on the next {@code get()} call.
	 *
	 * @param ttl duration until entry expires
	 * @return this builder for chaining
	 */
	public Builder<T> ttl(Duration ttl)
	{
		this.ttl = ttl.toNanos();
		predicate = predicate != null
				? predicate.and(e -> System.nanoTime() < e.ttl())
				: e -> System.nanoTime() < e.ttl();
		return this;
	}

	/**
	 * Adds custom validation on the cached value.
	 *
	 * <p>
	 * If the predicate returns {@code false}, the entry is considered invalid and will be regenerated on the next
	 * {@code get()} call.
	 *
	 * <p>
	 * Multiple calls to this method chain predicates with AND logic.
	 *
	 * @param predicate function that validates the stored value
	 * @return this builder for chaining
	 */
	public Builder<T> predicate(Predicate<T> predicate)
	{
		this.predicate = this.predicate != null
				? this.predicate.and((gate.cache.Entry<T> e) -> predicate.test(e.value()))
				: e -> predicate.test(e.value());
		return this;
	}

	/**
	 * Sets the retry period when generating a new value fails.
	 *
	 * <p>
	 * If the generator throws an exception, the cache:
	 * <ol>
	 * <li>Keeps the old value (if exists)
	 * <li>Marks when it can retry regeneration
	 * <li>During retry period, the old value is only returned if it still passes TTL and predicate validations
	 * </ol>
	 *
	 * <p>
	 * Without retry configured, exceptions are propagated immediately.
	 *
	 * @param retry duration to wait before attempting regeneration
	 * @return this builder for chaining
	 */
	public Builder<T> retry(Duration retry)
	{
		this.retry = retry.toNanos();

		predicate = predicate != null
				? predicate.and(e -> e.retry() == -1 || System.nanoTime() < e.retry())
				: e -> e.retry() == -1 || System.nanoTime() < e.retry();
		return this;
	}

	/**
	 * Builds the cache with the specified configuration.
	 *
	 * @return configured cache ready for use
	 */
	public Cache<T> build()
	{

		return predicate != null
				? new LazyCache<>(generator, predicate, ttl, retry)
				: new SimpleCache<>(generator);
	}
}
