package gate.lang.expression;

import gate.converter.Converter;
import gate.error.ExpressionException;
import gate.lang.property.Property;
import gate.lang.property.PropertyScanner;
import gate.lang.template.Evaluable;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;

public class Expression implements Evaluable
{

	private static final Pattern PATTERN = Pattern.compile("(?:^|\\.)([_$a-zA-Z][_$a-zA-Z0-9]*)\\(\\)|(?:^|\\.)([_$a-zA-Z][_$a-zA-Z0-9]*)|\\[(\\d+)\\]|\\[\"([^\"]+)\"\\]|(.+)");

	private Object current;
	private List<Object> context;
	private Parameters parameters;

	private int index = 0;
	private final String value;
	private final List<Object> tokens;

	private Expression(String value, List<Object> tokens)
	{
		this.value = value;
		this.tokens = tokens;
	}

	public static Expression of(String value)
	{
		List<Object> tokens = new ArrayList<>();
		try (ExpressionScanner scanner = new ExpressionScanner(value))
		{
			for (Object token = scanner.next(); token != ExpressionToken.EOF; token = scanner.next())
				tokens.add(token);
			tokens.add(ExpressionToken.EOF);
			return new Expression(value, tokens);
		} catch (IOException ex)
		{
			throw new ExpressionException("Error trying to evaluate expression", ex.getMessage());
		}
	}

	private void next()
	{
		current = tokens.get(index++);
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws ExpressionException
	{
		try
		{
			writer.write(Converter.toText(evaluate(context, parameters)));
		} catch (IOException ex)
		{
			throw new ExpressionException("Error trying to evaluate expression", ex.getMessage());
		}
	}

	public Object evaluate(Object context) throws ExpressionException
	{
		return evaluate(new ArrayList<>(Collections.singletonList(context)), new Parameters());
	}

	public Object evaluate(List<Object> context, Parameters parameters) throws ExpressionException
	{
		index = 0;
		this.context = context;
		this.parameters = parameters;

		next();
		Object result = expression();
		if (current != ExpressionToken.EOF)
			throw new ExpressionException("Expected \"%s\" and found \"%s\" on expression \"%s\".",
					ExpressionToken.EOF, current, value);
		return result;
	}

	private Object expression() throws ExpressionException
	{
		Object result = sentence();

		while (current == ExpressionToken.AND
				|| current == ExpressionToken.OR)
		{

			next();
			Object v = sentence();

			if (!(result instanceof Boolean))
				throw new ExpressionException("Expected \"Boolean\" and found \"%s\" on expression \"%s\".",
						result, value);
			if (!(v instanceof Boolean))
				throw new ExpressionException("Expected \"Boolean\" and found \"%s\" on expression \"%s\".",
						result, value);

			if (current == ExpressionToken.AND)
				result = ((Boolean) result) & (Boolean) v;
			else
				result = ((Boolean) result) | (Boolean) v;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Object sentence() throws ExpressionException
	{
		Object result = comparable();

		while (ExpressionToken.EQ.equals(current)
				|| ExpressionToken.NE.equals(current)
				|| ExpressionToken.GE.equals(current)
				|| ExpressionToken.GT.equals(current)
				|| ExpressionToken.LE.equals(current)
				|| ExpressionToken.LT.equals(current)
				|| ExpressionToken.RX.equals(current))
		{

			ExpressionToken operator = (ExpressionToken) current;
			next();
			Object v = comparable();

			switch (operator)
			{
				case EQ ->
					result = Objects.equals(result, v);
				case NE ->
					result = !Objects.equals(result, v);
				case RX ->
				{
					if (result instanceof String && v instanceof String)
						result = ((String) result).matches((String) v);
					else
						throw new ExpressionException(
								"Expected \"pattern string\" and found \"%s\" on expression \"%s\".", v, value);
				}
				case GE, GT, LE, LT ->
				{

					if (result instanceof Comparable comparable)
						if (operator == ExpressionToken.GE)
							result = comparable.compareTo(v) >= 0;
						else if (operator == ExpressionToken.GT)
							result = comparable.compareTo(v) > 0;
						else if (operator == ExpressionToken.LE)
							result = comparable.compareTo(v) <= 0;
						else
							result = comparable.compareTo(v) < 0;
					else
						throw new ExpressionException("Expected \"Comparable\" and found \"%s\" on expression \"%s\".", result, value);
				}

				default ->
					throw new ExpressionException("Unexpected token \"%s\" found on expression \"%s\".", result, value);

			}
		}

		return result;
	}

	private Object comparable() throws ExpressionException
	{
		Object result = term();

		while (current == ExpressionToken.ADD
				|| current == ExpressionToken.SUB)
		{

			if (current == ExpressionToken.ADD)
			{
				next();
				result = ExpressionCalculator.add(result, term());
			} else if (current == ExpressionToken.SUB)
			{
				next();
				result = ExpressionCalculator.sub(result, term());
			}
		}

		return result;
	}

	private Object term() throws ExpressionException
	{
		Object result = not();

		while (current == ExpressionToken.MUL
				|| current == ExpressionToken.DIV)
		{
			if (current == ExpressionToken.MUL)
			{
				next();
				result = ExpressionCalculator.mul(result, not());
			} else
			{
				next();
				result = ExpressionCalculator.div(result, not());
			}
		}

		return result;
	}

	private Object not() throws ExpressionException
	{
		if (ExpressionToken.NOT.equals(current))
		{
			next();
			Object v = not();
			if (!(v instanceof Boolean))
				throw new ExpressionException("Expected \"Boolean\" and found \"%s\" on expression \"%s\".", v, value);
			return !(Boolean) v;
		}
		return unary();

	}

	private Object unary() throws ExpressionException
	{
		if (ExpressionToken.EMPTY.equals(current))
		{
			next();
			Object v = signed();
			if (v instanceof String string)
				return string.isEmpty();
			else if (v instanceof Object[] objects)
				return objects.length == 0;
			else if (v instanceof Collection<?> collection)
				return collection.isEmpty();
			else if (v instanceof Map<?, ?> map)
				return map.isEmpty();
			else
				throw new ExpressionException(
						"Expected \"Array, Collection, Map ou String\" and found \"%s\" on expression \"%s\".", v,
						value);
		} else if (ExpressionToken.SIZE.equals(current))
		{
			next();
			Object v = signed();
			if (v instanceof String)
				return ((CharSequence) v).length();
			else if (v instanceof Object[] objects)
				return objects.length;
			else if (v instanceof Collection<?> collection)
				return collection.size();
			else if (v instanceof Map<?, ?> map)
				return map.size();
			else
				throw new ExpressionException(
						"Expected \"Array, Collection, Map or String\" and found \"%s\" on expression \"%s\".", v,
						value);
		}

		return signed();
	}

	private Object signed() throws ExpressionException
	{
		if (ExpressionToken.ADD.equals(current))
		{
			next();
			return ExpressionCalculator.mul(1, factor());
		} else if (ExpressionToken.SUB.equals(current))
		{
			next();
			return ExpressionCalculator.mul(-1, factor());
		} else
			return factor();

	}

	private Object factor() throws ExpressionException
	{
		if (ExpressionToken.OPEN_PARENTHESES.equals(current))
		{
			next();
			Object result = expression();
			if (!ExpressionToken.CLOSE_PARENTHESES.equals(current))
				throw new ExpressionException("Expected \"%s\" and found  \"%s\" on expression \"%s\".",
						ExpressionToken.CLOSE_PARENTHESES, current, value);
			next();
			return result;
		} else if (ExpressionToken.VARIABLE.equals(current))
			return variable();
		else if (ExpressionToken.CONTEXT.equals(current))
			return context();
		else if (current instanceof StringBuilder)
			return property();
		else if (current instanceof ExpressionToken)
			throw new ExpressionException("Expected \"Object\" and found \"%s\" on expression \"%s\".", current, value);

		Object result = current;
		next();
		return result;
	}

	private Object context() throws ExpressionException
	{
		if (context.size() == 1)
			throw new ExpressionException("Invalid context for the expression \"%s\".", value);

		Object object = context.remove(0);
		var values = parameters.poll();

		next();

		Object result
				= ExpressionToken.CONTEXT == current
						? context()
						: current == ExpressionToken.VARIABLE ? variable() : property();

		context.add(0, object);
		parameters.push(values);

		return result;
	}

	private Object property() throws ExpressionException
	{
		String name = name();
		Object object = context.get(0);
		return Property.evaluate(name, object);
	}

	private Object variable() throws ExpressionException
	{
		next();
		try (PropertyScanner propertyScanner = new PropertyScanner(name()))
		{
			Object token = propertyScanner.next();
			if (!(token instanceof String))
				throw new ExpressionException("Expected \"Java Identifier\" and found \"%s\" on expression \"%s\".",
						token, value);

			Object object = parameters.get((String) token);

			if (object == null)
				return "";

			StringBuilder property = new StringBuilder("this");
			for (token = propertyScanner.next(); token != null; token = propertyScanner.next())
				property.append(token);

			return Property.getProperty(object.getClass(), property.toString()).getValue(object);
		} catch (IOException e)
		{
			throw new ExpressionException("Error trying to evaluate expression: %s.", value);
		}
	}

	private String name() throws ExpressionException
	{
		if (!(current instanceof StringBuilder))
			throw new ExpressionException("Expected \"Java Identifier\" and found \"%s\" on expression \"%s\".",
					current,
					value);

		StringBuilder string = new StringBuilder(current.toString());

		next();

		if (ExpressionToken.OPEN_PARENTHESES.equals(current))
			string.append(argumentList());

		if (ExpressionToken.DOT.equals(current))
		{
			next();
			string.append(".").append(name());
		} else if (ExpressionToken.OPEN_BRACKET.equals(current))
			string.append(indx());

		return string.toString();
	}

	private String indx() throws ExpressionException
	{
		StringBuilder string = new StringBuilder("[");

		next();
		if (current instanceof Number)
			string.append(current);
		else if (current instanceof String)
			string.append("'").append(current).append("'");
		else
			throw new ExpressionException("Expected \"Number or String\" and found \"%s\" on expression \"%s\".",
					current, value);

		next();
		if (!ExpressionToken.CLOSE_BRACKET.equals(current))
			throw new ExpressionException("Expected \"]\" and found \"%s\" on expression \"%s\".", current, value);

		string.append("]");

		next();

		if (ExpressionToken.DOT.equals(current))
		{
			next();
			string.append(".").append(name());
		}

		return string.toString();
	}

	private String argumentList() throws ExpressionException
	{
		StringBuilder string = new StringBuilder("(");

		next();
		while (!ExpressionToken.CLOSE_PARENTHESES.equals(current))
			string.append(arguments());

		if (!ExpressionToken.CLOSE_PARENTHESES.equals(current))
			throw new ExpressionException("Expected \"]\" and found \"%s\" on expression \"%s\".", current, value);
		string.append(")");

		next();

		return string.toString();
	}

	private String arguments() throws ExpressionException
	{
		StringBuilder string = new StringBuilder();

		Object value = expression();
		if (value instanceof String)
			string.append("'").append(value.toString()).append("'");
		else if (value != null)
			string.append(value);

		if (ExpressionToken.COMMA.equals(current))
		{
			next();
			string.append(",");
			string.append(arguments());
		}

		return string.toString();
	}

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Expression
				&& value.equals(((Expression) obj).value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}
