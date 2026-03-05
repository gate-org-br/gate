package gate.function;

import java.util.function.*;

/**
 * Utility methods that adapt int-focused Try* functional interfaces to standard java.util.function types.
 */
public class TryInt
{

	public static IntBinaryOperator of(TryIntBinaryOperator operator)
	{
		return TryIntBinaryOperator.wrap(operator);
	}

	public static IntConsumer of(TryIntConsumer consumer)
	{
		return TryIntConsumer.wrap(consumer);
	}

	public static <T> IntFunction<T> of(TryIntFunction<T> function)
	{
		return TryIntFunction.wrap(function);
	}

	public static IntPredicate of(TryIntPredicate predicate)
	{
		return TryIntPredicate.wrap(predicate);
	}

	public static IntSupplier of(TryIntSupplier supplier)
	{
		return TryIntSupplier.wrap(supplier);
	}

	public static <T> ToIntFunction<T> of(TryToIntFunction<T> function)
	{
		return TryToIntFunction.wrap(function);
	}

	public static IntUnaryOperator of(TryIntUnaryOperator operator)
	{
		return TryIntUnaryOperator.wrap(operator);
	}
}
