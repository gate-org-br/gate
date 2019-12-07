package gate.lang.expression;

import gate.error.ExpressionException;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ExpressionCalculator
{

	static Object add(Object term1, Object term2) throws ExpressionException
	{
		if (term1 instanceof String)
		{
			if (term2 instanceof String)
				return term1.toString() + term2;
		} else if (term1 instanceof Byte)
		{
			if (term2 instanceof Byte)
				return (Byte) term1 + (Byte) term2;
			else if (term2 instanceof Short)
				return (Byte) term1 + (Short) term2;
			else if (term2 instanceof Integer)
				return (Byte) term1 + (Integer) term2;
			else if (term2 instanceof Long)
				return (Byte) term1 + (Long) term2;
			else if (term2 instanceof Float)
				return (Byte) term1 + (Float) term2;
			else if (term2 instanceof Double)
				return (Byte) term1 + (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Byte) term1).add((BigDecimal) term2);
		} else if (term1 instanceof Short)
		{
			if (term2 instanceof Byte)
				return (Short) term1 + (Byte) term2;
			else if (term2 instanceof Short)
				return (Short) term1 + (Short) term2;
			else if (term2 instanceof Integer)
				return (Short) term1 + (Integer) term2;
			else if (term2 instanceof Long)
				return (Short) term1 + (Long) term2;
			else if (term2 instanceof Float)
				return (Short) term1 + (Float) term2;
			else if (term2 instanceof Double)
				return (Short) term1 + (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Short) term1).add((BigDecimal) term2);
		} else if (term1 instanceof Integer)
		{
			if (term2 instanceof Byte)
				return (Integer) term1 + (Byte) term2;
			else if (term2 instanceof Short)
				return (Integer) term1 + (Short) term2;
			else if (term2 instanceof Integer)
				return (Integer) term1 + (Integer) term2;
			else if (term2 instanceof Long)
				return (Integer) term1 + (Long) term2;
			else if (term2 instanceof Float)
				return (Integer) term1 + (Float) term2;
			else if (term2 instanceof Double)
				return (Integer) term1 + (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Integer) term1).add((BigDecimal) term2);
		} else if (term1 instanceof Long)
		{
			if (term2 instanceof Byte)
				return (Long) term1 + (Byte) term2;
			else if (term2 instanceof Short)
				return (Long) term1 + (Short) term2;
			else if (term2 instanceof Integer)
				return (Long) term1 + (Integer) term2;
			else if (term2 instanceof Long)
				return (Long) term1 + (Long) term2;
			else if (term2 instanceof Float)
				return (Long) term1 + (Float) term2;
			else if (term2 instanceof Double)
				return (Long) term1 + (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Long) term1).add((BigDecimal) term2);
		} else if (term1 instanceof Float)
		{
			if (term2 instanceof Byte)
				return (Float) term1 + (Byte) term2;
			else if (term2 instanceof Short)
				return (Float) term1 + (Short) term2;
			else if (term2 instanceof Integer)
				return (Float) term1 + (Integer) term2;
			else if (term2 instanceof Long)
				return (Float) term1 + (Long) term2;
			else if (term2 instanceof Float)
				return (Float) term1 + (Float) term2;
			else if (term2 instanceof Double)
				return (Float) term1 + (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Float) term1).add((BigDecimal) term2);
		} else if (term1 instanceof Double)
		{
			if (term2 instanceof Byte)
				return (Double) term1 + (Byte) term2;
			else if (term2 instanceof Short)
				return (Double) term1 + (Short) term2;
			else if (term2 instanceof Integer)
				return (Double) term1 + (Integer) term2;
			else if (term2 instanceof Long)
				return (Double) term1 + (Long) term2;
			else if (term2 instanceof Float)
				return (Double) term1 + (Float) term2;
			else if (term2 instanceof Double)
				return (Double) term1 + (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Double) term1).add((BigDecimal) term2);
		}
		throw new ExpressionException("Tentativa de somar dados incompatíveis.");
	}

	static Object sub(Object term1, Object term2) throws ExpressionException
	{
		if (term1 instanceof Byte)
		{
			if (term2 instanceof Byte)
				return (Byte) term1 - (Byte) term2;
			else if (term2 instanceof Short)
				return (Byte) term1 - (Short) term2;
			else if (term2 instanceof Integer)
				return (Byte) term1 - (Integer) term2;
			else if (term2 instanceof Long)
				return (Byte) term1 - (Long) term2;
			else if (term2 instanceof Float)
				return (Byte) term1 - (Float) term2;
			else if (term2 instanceof Double)
				return (Byte) term1 - (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Byte) term1).subtract((BigDecimal) term2);
		} else if (term1 instanceof Short)
		{
			if (term2 instanceof Byte)
				return (Short) term1 - (Byte) term2;
			else if (term2 instanceof Short)
				return (Short) term1 - (Short) term2;
			else if (term2 instanceof Integer)
				return (Short) term1 - (Integer) term2;
			else if (term2 instanceof Long)
				return (Short) term1 - (Long) term2;
			else if (term2 instanceof Float)
				return (Short) term1 - (Float) term2;
			else if (term2 instanceof Double)
				return (Short) term1 - (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Short) term1).subtract((BigDecimal) term2);
		} else if (term1 instanceof Integer)
		{
			if (term2 instanceof Byte)
				return (Integer) term1 - (Byte) term2;
			else if (term2 instanceof Short)
				return (Integer) term1 - (Short) term2;
			else if (term2 instanceof Integer)
				return (Integer) term1 - (Integer) term2;
			else if (term2 instanceof Long)
				return (Integer) term1 - (Long) term2;
			else if (term2 instanceof Float)
				return (Integer) term1 - (Float) term2;
			else if (term2 instanceof Double)
				return (Integer) term1 - (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Integer) term1).subtract((BigDecimal) term2);
		} else if (term1 instanceof Long)
		{
			if (term2 instanceof Byte)
				return (Long) term1 - (Byte) term2;
			else if (term2 instanceof Short)
				return (Long) term1 - (Short) term2;
			else if (term2 instanceof Integer)
				return (Long) term1 - (Integer) term2;
			else if (term2 instanceof Long)
				return (Long) term1 - (Long) term2;
			else if (term2 instanceof Float)
				return (Long) term1 - (Float) term2;
			else if (term2 instanceof Double)
				return (Long) term1 - (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Long) term1).subtract((BigDecimal) term2);
		} else if (term1 instanceof Float)
		{
			if (term2 instanceof Byte)
				return (Float) term1 - (Byte) term2;
			else if (term2 instanceof Short)
				return (Float) term1 - (Short) term2;
			else if (term2 instanceof Integer)
				return (Float) term1 - (Integer) term2;
			else if (term2 instanceof Long)
				return (Float) term1 - (Long) term2;
			else if (term2 instanceof Float)
				return (Float) term1 - (Float) term2;
			else if (term2 instanceof Double)
				return (Float) term1 - (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Float) term1).subtract((BigDecimal) term2);
		} else if (term1 instanceof Double)
		{
			if (term2 instanceof Byte)
				return (Double) term1 - (Byte) term2;
			else if (term2 instanceof Short)
				return (Double) term1 - (Short) term2;
			else if (term2 instanceof Integer)
				return (Double) term1 - (Integer) term2;
			else if (term2 instanceof Long)
				return (Double) term1 - (Long) term2;
			else if (term2 instanceof Float)
				return (Double) term1 - (Float) term2;
			else if (term2 instanceof Double)
				return (Double) term1 - (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Double) term1).subtract((BigDecimal) term2);
		}
		throw new ExpressionException("Tentativa de subtrair dados incompatíveis.");
	}

	static Object mul(Object term1, Object term2) throws ExpressionException
	{
		if (term1 instanceof String)
		{
			if (term2 instanceof Byte)
				return Stream.generate(() -> (String) term1).limit((Byte) term2)
						.collect(Collectors.joining());
			else if (term2 instanceof Short)
				return Stream.generate(() -> (String) term1).limit((Short) term2)
						.collect(Collectors.joining());
			else if (term2 instanceof Integer)
				return Stream.generate(() -> (String) term1).limit((Integer) term2)
						.collect(Collectors.joining());
			else if (term2 instanceof Long)
				return Stream.generate(() -> (String) term1).limit((Long) term2)
						.collect(Collectors.joining());
		} else if (term1 instanceof Byte)
		{
			if (term2 instanceof String)
				Stream.generate(() -> (String) term2).limit((Byte) term1)
						.collect(Collectors.joining());
			else if (term2 instanceof Byte)
				return (Byte) term1 * (Byte) term2;
			else if (term2 instanceof Short)
				return (Byte) term1 * (Short) term2;
			else if (term2 instanceof Integer)
				return (Byte) term1 * (Integer) term2;
			else if (term2 instanceof Long)
				return (Byte) term1 * (Long) term2;
			else if (term2 instanceof Float)
				return (Byte) term1 * (Float) term2;
			else if (term2 instanceof Double)
				return (Byte) term1 * (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Byte) term1).multiply((BigDecimal) term2);
		} else if (term1 instanceof Short)
		{
			if (term2 instanceof String)
				Stream.generate(() -> (String) term2).limit((Short) term1)
						.collect(Collectors.joining());
			else if (term2 instanceof Byte)
				return (Short) term1 * (Byte) term2;
			else if (term2 instanceof Short)
				return (Short) term1 * (Short) term2;
			else if (term2 instanceof Integer)
				return (Short) term1 * (Integer) term2;
			else if (term2 instanceof Long)
				return (Short) term1 * (Long) term2;
			else if (term2 instanceof Float)
				return (Short) term1 * (Float) term2;
			else if (term2 instanceof Double)
				return (Short) term1 * (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Short) term1).multiply((BigDecimal) term2);
		} else if (term1 instanceof Integer)
		{
			if (term2 instanceof String)
				Stream.generate(() -> (String) term2).limit((Integer) term1)
						.collect(Collectors.joining());
			else if (term2 instanceof Byte)
				return (Integer) term1 * (Byte) term2;
			else if (term2 instanceof Short)
				return (Integer) term1 * (Short) term2;
			else if (term2 instanceof Integer)
				return (Integer) term1 * (Integer) term2;
			else if (term2 instanceof Long)
				return (Integer) term1 * (Long) term2;
			else if (term2 instanceof Float)
				return (Integer) term1 * (Float) term2;
			else if (term2 instanceof Double)
				return (Integer) term1 * (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Integer) term1).multiply((BigDecimal) term2);
		} else if (term1 instanceof Long)
		{
			if (term2 instanceof String)
				Stream.generate(() -> (String) term2).limit((Long) term1)
						.collect(Collectors.joining());
			else if (term2 instanceof Byte)
				return (Long) term1 * (Byte) term2;
			else if (term2 instanceof Short)
				return (Long) term1 * (Short) term2;
			else if (term2 instanceof Integer)
				return (Long) term1 * (Integer) term2;
			else if (term2 instanceof Long)
				return (Long) term1 * (Long) term2;
			else if (term2 instanceof Float)
				return (Long) term1 * (Float) term2;
			else if (term2 instanceof Double)
				return (Long) term1 * (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Long) term1).multiply((BigDecimal) term2);
		} else if (term1 instanceof Float)
		{
			if (term2 instanceof Byte)
				return (Float) term1 * (Byte) term2;
			else if (term2 instanceof Short)
				return (Float) term1 * (Short) term2;
			else if (term2 instanceof Integer)
				return (Float) term1 * (Integer) term2;
			else if (term2 instanceof Long)
				return (Float) term1 * (Long) term2;
			else if (term2 instanceof Float)
				return (Float) term1 * (Float) term2;
			else if (term2 instanceof Double)
				return (Float) term1 * (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Float) term1).multiply((BigDecimal) term2);
		} else if (term1 instanceof Double)
		{
			if (term2 instanceof Byte)
				return (Double) term1 * (Byte) term2;
			else if (term2 instanceof Short)
				return (Double) term1 * (Short) term2;
			else if (term2 instanceof Integer)
				return (Double) term1 * (Integer) term2;
			else if (term2 instanceof Long)
				return (Double) term1 * (Long) term2;
			else if (term2 instanceof Float)
				return (Double) term1 * (Float) term2;
			else if (term2 instanceof Double)
				return (Double) term1 * (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Double) term1).multiply((BigDecimal) term2);
		}
		throw new ExpressionException("Tentativa de multiplicar dados incompatíveis.");
	}

	static Object div(Object term1, Object term2) throws ExpressionException
	{
		if (term1 instanceof Byte)
		{
			if (term2 instanceof Byte)
				return (Byte) term1 / (Byte) term2;
			else if (term2 instanceof Short)
				return (Byte) term1 / (Short) term2;
			else if (term2 instanceof Integer)
				return (Byte) term1 / (Integer) term2;
			else if (term2 instanceof Long)
				return (Byte) term1 / (Long) term2;
			else if (term2 instanceof Float)
				return (Byte) term1 / (Float) term2;
			else if (term2 instanceof Double)
				return (Byte) term1 / (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Byte) term1).divide((BigDecimal) term2);
		} else if (term1 instanceof Short)
		{
			if (term2 instanceof Byte)
				return (Short) term1 / (Byte) term2;
			else if (term2 instanceof Short)
				return (Short) term1 / (Short) term2;
			else if (term2 instanceof Integer)
				return (Short) term1 / (Integer) term2;
			else if (term2 instanceof Long)
				return (Short) term1 / (Long) term2;
			else if (term2 instanceof Float)
				return (Short) term1 / (Float) term2;
			else if (term2 instanceof Double)
				return (Short) term1 / (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Short) term1).divide((BigDecimal) term2);
		} else if (term1 instanceof Integer)
		{
			if (term2 instanceof Byte)
				return (Integer) term1 / (Byte) term2;
			else if (term2 instanceof Short)
				return (Integer) term1 / (Short) term2;
			else if (term2 instanceof Integer)
				return (Integer) term1 / (Integer) term2;
			else if (term2 instanceof Long)
				return (Integer) term1 / (Long) term2;
			else if (term2 instanceof Float)
				return (Integer) term1 / (Float) term2;
			else if (term2 instanceof Double)
				return (Integer) term1 / (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Integer) term1).divide((BigDecimal) term2);
		} else if (term1 instanceof Long)
		{
			if (term2 instanceof Byte)
				return (Long) term1 / (Byte) term2;
			else if (term2 instanceof Short)
				return (Long) term1 / (Short) term2;
			else if (term2 instanceof Integer)
				return (Long) term1 / (Integer) term2;
			else if (term2 instanceof Long)
				return (Long) term1 / (Long) term2;
			else if (term2 instanceof Float)
				return (Long) term1 / (Float) term2;
			else if (term2 instanceof Double)
				return (Long) term1 / (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Long) term1).divide((BigDecimal) term2);
		} else if (term1 instanceof Float)
		{
			if (term2 instanceof Byte)
				return (Float) term1 / (Byte) term2;
			else if (term2 instanceof Short)
				return (Float) term1 / (Short) term2;
			else if (term2 instanceof Integer)
				return (Float) term1 / (Integer) term2;
			else if (term2 instanceof Long)
				return (Float) term1 / (Long) term2;
			else if (term2 instanceof Float)
				return (Float) term1 / (Float) term2;
			else if (term2 instanceof Double)
				return (Float) term1 / (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Float) term1).divide((BigDecimal) term2);
		} else if (term1 instanceof Double)
		{
			if (term2 instanceof Byte)
				return (Double) term1 / (Byte) term2;
			else if (term2 instanceof Short)
				return (Double) term1 / (Short) term2;
			else if (term2 instanceof Integer)
				return (Double) term1 / (Integer) term2;
			else if (term2 instanceof Long)
				return (Double) term1 / (Long) term2;
			else if (term2 instanceof Float)
				return (Double) term1 / (Float) term2;
			else if (term2 instanceof Double)
				return (Double) term1 / (Double) term2;
			else if (term2 instanceof BigDecimal)
				return BigDecimal.valueOf((Double) term1).divide((BigDecimal) term2);
		}
		throw new ExpressionException("Tentativa de dividir dados incompatíveis.");
	}

}
