package gate.function;

import java.util.Comparator;
import java.util.function.*;

/**
 * Utility methods that adapt Try* functional interfaces to standard java.util.function types.
 */
public class Try
{

	public static <T, U> BiConsumer<T, U> of(TryBiConsumer<T, U> consumer)
	{
		return TryBiConsumer.wrap(consumer);
	}

	public static <T, U, R> BiFunction<T, U, R> of(TryBiFunction<T, U, R> function)
	{
		return TryBiFunction.wrap(function);
	}

	public static <T> BinaryOperator<T> of(TryBinaryOperator<T> operator)
	{
		return TryBinaryOperator.wrap(operator);
	}

	public static <T, R> Function<T, R> of(TryFunction<T, R> function)
	{
		return TryFunction.wrap(function);
	}

	public static <T> Comparator<T> of(TryComparator<T> comparator)
	{
		return TryComparator.wrap(comparator);
	}

	public static <T> Consumer<T> of(TryConsumer<T> consumer)
	{
		return TryConsumer.wrap(consumer);
	}

	public static <T> Predicate<T> of(TryPredicate<T> predicate)
	{
		return TryPredicate.wrap(predicate);
	}

	public static Runnable of(TryRunnable runnable)
	{
		return TryRunnable.wrap(runnable);
	}

	public static <T> Supplier<T> of(TrySupplier<? extends T> supplier)
	{
		return TrySupplier.wrap(supplier);
	}

	public static <T> UnaryOperator<T> of(TryUnaryOperator<T> operator)
	{
		return TryUnaryOperator.wrap(operator);
	}
}
