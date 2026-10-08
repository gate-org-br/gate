package gate.cache;

/**
 * Lazy cache with automatic value generation, TTL support and retry mechanism.
 *
 * <p>
 * Thread-safe implementation that generates values on-demand and caches them until they expire or become invalid.
 *
 * <p>
 * Usage example:
 * <pre>{@code
 * Cache<UserData> cache = Cache.builder(() -> fetchUserFromDB())
 *     .ttl(Duration.ofMinutes(5))
 *     .retry(Duration.ofSeconds(30))
 *     .build();
 *
 * UserData user = cache.get();  // generates and caches
 * UserData same = cache.get();  // returns cached value
 *
 * cache.invalidate();           // forces regeneration
 * UserData fresh = cache.get(); // generates new value
 * }</pre>
 *
 * @param <T> type of the cached value
 */
public interface Cache<T>
{

	/**
	 * Returns the cached value, generating it if necessary.
	 *
	 * <p>
	 * The value is regenerated if:
	 * <ul>
	 * <li>No value is cached yet
	 * <li>TTL has expired
	 * <li>Custom predicate validation fails
	 * <li>Retry period has expired after a previous failure
	 * </ul>
	 *
	 * @return the cached or newly generated value
	 * @throws RuntimeException if generation fails and no retry is configured, or if retry period has expired
	 */
	T get();

	/**
	 * Invalidates the cached value, forcing regeneration on next {@code get()} call.
	 *
	 * <p>
	 * This method is thread-safe and can be called concurrently with {@code get()}. Use this when you know the cached
	 * data is stale and should be refreshed immediately.
	 */
	void invalidate();

	/**
	 * Creates a new builder for configuring a cache.
	 *
	 * @param <T> type of the cached value
	 * @param generator function that generates the cached value
	 * @return builder instance for configuration
	 */
	static <T> Builder<T> builder(Generator<T> generator)
	{
		return new Builder<>(generator);
	}
}
