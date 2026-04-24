package gate.function;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding Gate functional type.
 */
@FunctionalInterface
public interface TryTriFunction<T, U, V, R>
{

	R apply(T t, U u, V v) throws Exception;

	static <T, U, V, R> TriFunction<T, U, V, R> wrap(TryTriFunction<T, U, V, R> function)
	{
		return (t, u, v) ->
		{
			try
			{
				return function.apply(t, u, v);
			} catch (RuntimeException e)
			{
				throw e;
			} catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		};
	}
}
