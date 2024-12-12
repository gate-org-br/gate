package gate.function;

import java.util.*;
import java.util.function.*;

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

	public static IntBinaryOperator ofInt(TryIntBinaryOperator operator)
	{
		return TryIntBinaryOperator.wrap(operator);
	}

	public static LongBinaryOperator ofLong(TryLongBinaryOperator operator)
	{
		return TryLongBinaryOperator.wrap(operator);
	}

	public static DoubleBinaryOperator ofDouble(TryDoubleBinaryOperator operator)
	{
		return TryDoubleBinaryOperator.wrap(operator);
	}

	public static <T> Comparator<T> of(TryComparator<T> comparator)
	{
		return TryComparator.wrap(comparator);
	}

	public static <T> Consumer<T> of(TryConsumer<T> consumer)
	{
		return TryConsumer.wrap(consumer);
	}

	public static IntConsumer ofInt(TryIntConsumer consumer)
	{
		return TryIntConsumer.wrap(consumer);
	}

	public static LongConsumer ofLong(TryLongConsumer consumer)
	{
		return TryLongConsumer.wrap(consumer);
	}

	public static DoubleConsumer ofDouble(TryDoubleConsumer consumer)
	{
		return TryDoubleConsumer.wrap(consumer);
	}

	public static <T, R> Function<T, R> of(TryFunction<T, R> function)
	{
		return TryFunction.wrap(function);
	}

	public static <T> IntFunction<T> ofInt(TryIntFunction<T> function)
	{
		return TryIntFunction.wrap(function);
	}

	public static <T> LongFunction<T> ofLong(TryLongFunction<T> function)
	{
		return TryLongFunction.wrap(function);
	}

	public static <T> DoubleFunction<T> ofDouble(TryDoubleFunction<T> function)
	{
		return TryDoubleFunction.wrap(function);
	}

	public static <T> Predicate<T> of(TryPredicate<T> predicate)
	{
		return TryPredicate.wrap(predicate);
	}

	public static IntPredicate ofInt(TryIntPredicate predicate)
	{
		return TryIntPredicate.wrap(predicate);
	}

	public static LongPredicate ofLong(TryLongPredicate predicate)
	{
		return TryLongPredicate.wrap(predicate);
	}

	public static DoublePredicate ofDouble(TryDoublePredicate predicate)
	{
		return TryDoublePredicate.wrap(predicate);
	}

	public static Runnable of(TryRunnable runnable)
	{
		return TryRunnable.wrap(runnable);
	}

	public static <T> Supplier<T> of(TrySupplier<? extends T> supplier)
	{
		return TrySupplier.wrap(supplier);
	}

	public static IntSupplier ofInt(TryIntSupplier supplier)
	{
		return TryIntSupplier.wrap(supplier);
	}

	public static LongSupplier ofLong(TryLongSupplier supplier)
	{
		return TryLongSupplier.wrap(supplier);
	}

	public static DoubleSupplier ofDouble(TryDoubleSupplier supplier)
	{
		return TryDoubleSupplier.wrap(supplier);
	}

	public static <T> ToIntFunction<T> ofToInt(TryToIntFunction<T> function)
	{
		return TryToIntFunction.wrap(function);
	}

	public static <T> ToLongFunction<T> ofToLong(TryToLongFunction<T> function)
	{
		return TryToLongFunction.wrap(function);
	}

	public static <T> ToDoubleFunction<T> ofToDouble(TryToDoubleFunction<T> function)
	{
		return TryToDoubleFunction.wrap(function);
	}

	public static <T> UnaryOperator<T> of(TryUnaryOperator<T> operator)
	{
		return TryUnaryOperator.wrap(operator);
	}

	public static IntUnaryOperator ofInt(TryIntUnaryOperator operator)
	{
		return TryIntUnaryOperator.wrap(operator);
	}

	public static LongUnaryOperator ofLong(TryLongUnaryOperator operator)
	{
		return TryLongUnaryOperator.wrap(operator);
	}

	public static DoubleUnaryOperator ofDouble(TryDoubleUnaryOperator operator)
	{
		return TryDoubleUnaryOperator.wrap(operator);
	}
}
