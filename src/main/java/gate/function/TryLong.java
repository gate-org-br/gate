package gate.function;

import java.util.function.*;

/**
 * Utility methods that adapt long-focused Try* functional interfaces to standard java.util.function types.
 */
public class TryLong
{

	public static LongBinaryOperator of(TryLongBinaryOperator operator)
	{
		return TryLongBinaryOperator.wrap(operator);
	}

	public static LongConsumer of(TryLongConsumer consumer)
	{
		return TryLongConsumer.wrap(consumer);
	}

	public static <T> LongFunction<T> of(TryLongFunction<T> function)
	{
		return TryLongFunction.wrap(function);
	}

	public static LongPredicate of(TryLongPredicate predicate)
	{
		return TryLongPredicate.wrap(predicate);
	}

	public static LongSupplier of(TryLongSupplier supplier)
	{
		return TryLongSupplier.wrap(supplier);
	}

	public static <T> ToLongFunction<T> of(TryToLongFunction<T> function)
	{
		return TryToLongFunction.wrap(function);
	}

	public static LongUnaryOperator of(TryLongUnaryOperator operator)
	{
		return TryLongUnaryOperator.wrap(operator);
	}
}
