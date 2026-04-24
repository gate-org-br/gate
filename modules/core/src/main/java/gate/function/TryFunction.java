package gate.function;

import java.util.function.Function;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryFunction<T, R>
{

	R apply(T t) throws Exception;

	static <T, R> Function<T, R> wrap(TryFunction<T, R> function)
	{
		return t ->
		{
			try {return function.apply(t);} catch (RuntimeException ex) {throw ex;} catch (Exception ex) {throw new RuntimeException(ex);}
		};
	}
}