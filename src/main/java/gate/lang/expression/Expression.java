package gate.lang.expression;

import gate.converter.Converter;
import gate.error.ExpressionException;
import gate.lang.property.Property;
import gate.lang.property.PropertyScanner;
import gate.lang.template.Evaluable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Expression implements Evaluable
{

	private Object current;
	private final String value;
	private List<Object> context;
	private Map<String, Object> parameters;

	public Expression(String value)
	{
		this.value = value;
	}

	@Override
	public void evaluate(List<Object> context,
		Map<String, Object> parameters,
		Writer writer) throws ExpressionException
	{
		try
		{
			writer.write(Converter.toText(evaluate(context, parameters)));
		} catch (IOException ex)
		{
			throw new ExpressionException("Error trying to evaluate expression.", ex.getMessage());
		}
	}

	public Object evaluate(Object context) throws ExpressionException
	{
		return evaluate(new ArrayList<>(Arrays.asList(context)),
			new HashMap<>());
	}

	public Object evaluate(List<Object> context, Map<String, Object> parameters) throws ExpressionException
	{
		this.context = context;
		this.parameters = parameters;
		try (ExpressionScanner scanner = new ExpressionScanner(value))
		{
			current = scanner.next();
			Object result = expression(scanner);
			if (current != ExpressionToken.EOF)
				throw new ExpressionException("Expected \"%s\" and found \"%s\" on expression \"%s\".",
					ExpressionToken.EOF, current, value);
			return result;
		} catch (IOException e)
		{
			throw new ExpressionException("Error trying to evaluate expression: %s.", value);
		}
	}

	private Object expression(ExpressionScanner scanner) throws ExpressionException
	{
		Object result = sentence(scanner);

		while (ExpressionToken.AND.equals(current)
			|| ExpressionToken.OR.equals(current))
		{
			ExpressionToken operator = (ExpressionToken) current;
			current = scanner.next();
			Object v = sentence(scanner);

			if (!(result instanceof Boolean))
				throw new ExpressionException("Expected \"Boolean\" and found \"%s\" on expression \"%s\".",
					current, v);
			if (!(v instanceof Boolean))
				throw new ExpressionException("Expected \"Boolean\" and found \"%s\" on expression \"%s\".",
					current, v);

			switch (operator)
			{
				case AND:
					result = ((Boolean) result) & (Boolean) v;
					break;
				case OR:
					result = ((Boolean) result) | (Boolean) v;
					break;
			}
		}
		return result;
	}

	private Object sentence(ExpressionScanner scanner) throws ExpressionException
	{
		Object result = comparable(scanner);

		while (ExpressionToken.EQ.equals(current)
			|| ExpressionToken.NE.equals(current)
			|| ExpressionToken.GE.equals(current)
			|| ExpressionToken.GT.equals(current)
			|| ExpressionToken.LE.equals(current)
			|| ExpressionToken.LT.equals(current))
		{

			ExpressionToken operator = (ExpressionToken) current;
			current = scanner.next();
			Object v = comparable(scanner);

			switch (operator)
			{
				case EQ:
					result = Objects.equals(result, v);
					break;
				case NE:
					result = !Objects.equals(result, v);
					break;
				default:
					if (!(result instanceof Comparable))
						throw new ExpressionException("Expected \"Comparable\" and found \"%s\" on expression \"%s\".",
							current, v);

					switch (operator)
					{
						case GE:

							result = ((Comparable) result).compareTo(v) >= 0;
							break;
						case GT:
							result = ((Comparable) result).compareTo(v) > 0;
							break;
						case LE:
							result = ((Comparable) result).compareTo(v) <= 0;
							break;
						case LT:
							result = ((Comparable) result).compareTo(v) < 0;
							break;

					}
			}
		}

		return result;
	}

	private Object comparable(ExpressionScanner scanner) throws ExpressionException
	{
		Object result = term(scanner);

		while (ExpressionToken.ADD.equals(current)
			|| ExpressionToken.SUB.equals(current))
		{
			switch ((ExpressionToken) current)
			{
				case ADD:
					current = scanner.next();
					result = ExpressionCalculator.add(result, term(scanner));
					break;
				case SUB:
					current = scanner.next();
					result = ExpressionCalculator.sub(result, term(scanner));
					break;
			}
		}

		return result;
	}

	private Object term(ExpressionScanner scanner) throws ExpressionException
	{
		Object result = not(scanner);

		while (ExpressionToken.MUL.equals(current)
			|| ExpressionToken.DIV.equals(current))
		{
			switch ((ExpressionToken) current)
			{
				case MUL:
					current = scanner.next();
					result = ExpressionCalculator.mul(result, not(scanner));
					break;
				case DIV:
					current = scanner.next();
					result = ExpressionCalculator.div(result, not(scanner));
					break;
			}
		}

		return result;
	}

	private Object not(ExpressionScanner scanner) throws ExpressionException
	{
		if (ExpressionToken.NOT.equals(current))
		{
			current = scanner.next();
			Object v = not(scanner);
			if (!(v instanceof Boolean))
				throw new ExpressionException("Expected \"Boolean\" and found \"%s\" on expression \"%s\".", current, v);
			return !(Boolean) v;
		}
		return unary(scanner);

	}

	private Object unary(ExpressionScanner scanner) throws ExpressionException
	{
		if (ExpressionToken.EMPTY.equals(current))
		{
			current = scanner.next();
			Object v = signed(scanner);
			if (v instanceof String)
				return ((String) v).isEmpty();
			else if (v instanceof Object[])
				return ((Object[]) v).length == 0;
			else if (v instanceof Collection<?>)
				return ((Collection<?>) v).isEmpty();
			else if (v instanceof Map<?, ?>)
				return ((Map<?, ?>) v).isEmpty();
			else
				throw new ExpressionException(
					"Expected \"Array, Collection, Map ou String\" and found \"%s\" on expression \"%s\".", current,
					v);
		} else if (ExpressionToken.SIZE.equals(current))
		{
			current = scanner.next();
			Object v = signed(scanner);
			if (v instanceof String)
				return ((CharSequence) v).length();
			else if (v instanceof Object[])
				return ((Object[]) v).length;
			else if (v instanceof Collection<?>)
				return ((Collection<?>) v).size();
			else if (v instanceof Map<?, ?>)
				return ((Map<?, ?>) v).size();
			else
				throw new ExpressionException(
					"Expected \"Array, Collection, Map or String\" and found \"%s\" on expression \"%s\".", current,
					v);
		}

		return signed(scanner);
	}

	private Object signed(ExpressionScanner scanner) throws ExpressionException
	{
		if (ExpressionToken.ADD.equals(current))
		{
			current = scanner.next();
			return ExpressionCalculator.mul(1, factor(scanner));
		} else if (ExpressionToken.SUB.equals(current))
		{
			current = scanner.next();
			return ExpressionCalculator.mul(-1, factor(scanner));
		} else
			return factor(scanner);

	}

	private Object factor(ExpressionScanner scanner) throws ExpressionException
	{
		if (ExpressionToken.OPEN_PARENTHESES.equals(current))
		{
			current = scanner.next();
			Object result = expression(scanner);
			if (!ExpressionToken.CLOSE_PARENTHESES.equals(current))
				throw new ExpressionException("Expected \"%s\" and found  \"%s\" on expression \"%s\".",
					ExpressionToken.CLOSE_PARENTHESES, current, value);
			current = scanner.next();
			return result;
		} else if (ExpressionToken.VARIABLE.equals(current))
			return variable(scanner);
		else if (ExpressionToken.CONTEXT.equals(current))
			return context(scanner);
		else if (current instanceof StringBuilder)
			return property(scanner);
		else if (current instanceof ExpressionToken)
			throw new ExpressionException("Expected \"Object\" and found \"%s\" on expression \"%s\".", current, value);

		Object result = current;
		current = scanner.next();
		return result;
	}

	private Object context(ExpressionScanner scanner) throws ExpressionException
	{
		if (context.size() == 1)
			throw new ExpressionException("Invalid context for the expression \"%s\".", value);

		Object object = context.remove(0);

		current = scanner.next();

		Object result = ExpressionToken.CONTEXT.equals(current)
			? context(scanner) : property(scanner);

		context.add(0, object);

		return result;
	}

	private Object property(ExpressionScanner scanner) throws ExpressionException
	{
		String name = name(scanner);
		Object object = context.get(0);
		return Property
			.getProperty(object.getClass(), name)
			.getValue(object);
	}

	private Object variable(ExpressionScanner scanner) throws ExpressionException
	{
		current = scanner.next();
		try (PropertyScanner propertyScanner = new PropertyScanner(name(scanner)))
		{
			Object token = propertyScanner.next();
			if (!(token instanceof String))
				throw new ExpressionException("Expected \"Java Identifier\" and found \"%s\" on expression \"%s\".",
					token, value);

			Object object = parameters.get((String) token);

			StringBuilder property = new StringBuilder("this");
			for (token = propertyScanner.next(); token != null; token = propertyScanner.next())
				property.append(token.toString());

			return Property.getProperty(object.getClass(), property.toString()).getValue(object);
		} catch (IOException e)
		{
			throw new ExpressionException("Error trying to evaluate expression: %s.", value);
		}
	}

	private String name(ExpressionScanner scanner) throws ExpressionException
	{
		if (!(current instanceof StringBuilder))
			throw new ExpressionException("Expected \"Java Identifier\" and found \"%s\" on expression \"%s\".", current,
				value);

		StringBuilder string
			= new StringBuilder(current.toString());

		current = scanner.next();

		if (ExpressionToken.OPEN_PARENTHESES.equals(current))
			string.append(argumentList(scanner));

		if (ExpressionToken.DOT.equals(current))
		{
			current = scanner.next();
			string.append(".").append(name(scanner));
		} else if (ExpressionToken.OPEN_BRACKET.equals(current))
			string.append(indx(scanner));

		return string.toString();
	}

	private String indx(ExpressionScanner scanner) throws ExpressionException
	{
		StringBuilder string = new StringBuilder("[");

		current = scanner.next();
		if (current instanceof Number)
			string.append(current.toString());
		else if (current instanceof String)
			string.append("'").append(current.toString()).append("'");
		else
			throw new ExpressionException("Expected \"Number or String\" and found \"%s\" on expression \"%s\".",
				current, value);

		current = scanner.next();
		if (!ExpressionToken.CLOSE_BRACKET.equals(current))
			throw new ExpressionException("Expected \"]\" and found \"%s\" on expression \"%s\".", current, value);

		string.append("]");

		current = scanner.next();

		if (ExpressionToken.DOT.equals(current))
		{
			current = scanner.next();
			string.append(".").append(name(scanner));
		}

		return string.toString();
	}

	private String argumentList(ExpressionScanner scanner) throws ExpressionException
	{
		StringBuilder string = new StringBuilder("(");

		current = scanner.next();
		while (!ExpressionToken.CLOSE_PARENTHESES.equals(current))
			string.append(arguments(scanner));

		if (!ExpressionToken.CLOSE_PARENTHESES.equals(current))
			throw new ExpressionException("Expected \"]\" and found \"%s\" on expression \"%s\".", current, value);
		string.append(")");

		current = scanner.next();

		return string.toString();
	}

	private String arguments(ExpressionScanner scanner) throws ExpressionException
	{
		StringBuilder string = new StringBuilder();

		Object value = expression(scanner);
		if (value instanceof String)
			string.append("'").append(current.toString()).append("'");
		else
			string.append(value.toString());

		if (ExpressionToken.COMMA.equals(current))
		{
			current = scanner.next();
			string.append(arguments(scanner));
		}

		return string.toString();
	}

	@Override
	public String toString()
	{
		return value;
	}

}
