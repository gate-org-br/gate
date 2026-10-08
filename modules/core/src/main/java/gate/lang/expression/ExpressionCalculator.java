package gate.lang.expression;

import gate.error.ExpressionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.LongBinaryOperator;

class ExpressionCalculator
{

	static Object add(Object a, Object b) throws ExpressionException
	{
		if (a instanceof String sa && b instanceof String sb)
			return sa + sb;
		if (a instanceof Number n1 && b instanceof Number n2)
			return promote(n1, n2, Long::sum, Double::sum, BigDecimal::add);
		throw new ExpressionException("Cannot add incompatible types: %s and %s.", a, b);
	}

	static Object sub(Object a, Object b) throws ExpressionException
	{
		if (a instanceof Number n1 && b instanceof Number n2)
			return promote(n1, n2, (x, y) -> x - y, (x, y) -> x - y, BigDecimal::subtract);
		throw new ExpressionException("Cannot subtract incompatible types: %s and %s.", a, b);
	}

	static Object mul(Object a, Object b) throws ExpressionException
	{
		if (a instanceof String s && b instanceof Number n)
			return s.repeat(n.intValue());
		if (a instanceof Number n && b instanceof String s)
			return s.repeat(n.intValue());
		if (a instanceof Number n1 && b instanceof Number n2)
			return promote(n1, n2, (x, y) -> x * y, (x, y) -> x * y, BigDecimal::multiply);
		throw new ExpressionException("Cannot multiply incompatible types: %s and %s.", a, b);
	}

	static Object div(Object a, Object b) throws ExpressionException
	{
		if (a instanceof Number n1 && b instanceof Number n2)
			return promote(n1, n2, (x, y) -> x / y, (x, y) -> x / y,
					(bd1, bd2) -> bd1.divide(bd2, RoundingMode.HALF_EVEN));
		throw new ExpressionException("Cannot divide incompatible types: %s and %s.", a, b);
	}

	static Object mod(Object a, Object b) throws ExpressionException
	{
		if (a instanceof Number n1 && b instanceof Number n2)
			return promote(n1, n2, (x, y) -> x % y, (x, y) -> x % y, BigDecimal::remainder);
		throw new ExpressionException("Cannot compute modulo of incompatible types: %s and %s.", a, b);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static int compare(Object a, Object b) throws ExpressionException
	{
		if (a instanceof Comparable c)
			return c.compareTo(b);
		throw new ExpressionException("Cannot compare incompatible types: %s and %s.", a, b);
	}

	static Object pow(Object a, Object b) throws ExpressionException
	{
		if (a instanceof Number n1 && b instanceof Number n2)
		{
			double result = Math.pow(n1.doubleValue(), n2.doubleValue());
			if (a instanceof Double || b instanceof Double)
				return result;
			if (a instanceof Float || b instanceof Float)
				return (float) result;
			if (a instanceof Long || b instanceof Long)
				return (long) result;
			return (int) result;
		}
		throw new ExpressionException("Cannot exponentiate incompatible types: %s and %s.", a, b);
	}

	private static Object promote(Number a, Number b,
	                              LongBinaryOperator intOp,
	                              DoubleBinaryOperator floatOp,
	                              BinaryOperator<BigDecimal> bdOp)
	{
		if (a instanceof BigDecimal || b instanceof BigDecimal)
			return bdOp.apply(toBigDecimal(a), toBigDecimal(b));
		if (a instanceof Double || b instanceof Double)
			return floatOp.applyAsDouble(a.doubleValue(), b.doubleValue());
		if (a instanceof Float || b instanceof Float)
			return (float) floatOp.applyAsDouble(a.floatValue(), b.floatValue());
		if (a instanceof Long || b instanceof Long)
			return intOp.applyAsLong(a.longValue(), b.longValue());
		return (int) intOp.applyAsLong(a.intValue(), b.intValue());
	}

	private static BigDecimal toBigDecimal(Number n)
	{
		return n instanceof BigDecimal b ? b : BigDecimal.valueOf(n.doubleValue());
	}
}