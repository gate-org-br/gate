package gate.function;

import java.util.function.*;

/**
 * Utility methods that adapt double-focused Try* functional interfaces to standard java.util.function types.
 */
public class TryDouble
{

	public static DoubleBinaryOperator of(TryDoubleBinaryOperator operator)
	{
		return TryDoubleBinaryOperator.wrap(operator);
	}

	public static DoubleConsumer of(TryDoubleConsumer consumer)
	{
		return TryDoubleConsumer.wrap(consumer);
	}

	public static <T> DoubleFunction<T> of(TryDoubleFunction<T> function)
	{
		return TryDoubleFunction.wrap(function);
	}

	public static DoublePredicate of(TryDoublePredicate predicate)
	{
		return TryDoublePredicate.wrap(predicate);
	}

	public static DoubleSupplier of(TryDoubleSupplier supplier)
	{
		return TryDoubleSupplier.wrap(supplier);
	}

	public static <T> ToDoubleFunction<T> of(TryToDoubleFunction<T> function)
	{
		return TryToDoubleFunction.wrap(function);
	}

	public static DoubleUnaryOperator of(TryDoubleUnaryOperator operator)
	{
		return TryDoubleUnaryOperator.wrap(operator);
	}
}
