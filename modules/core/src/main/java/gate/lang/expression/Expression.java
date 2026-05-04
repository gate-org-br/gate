package gate.lang.expression;

import gate.adapter.converter.ExpressionConverter;
import gate.adapter.renderer.Renderer;
import gate.error.ExpressionException;
import gate.lang.property.Property;
import gate.lang.property.PropertyScanner;
import gate.lang.template.Evaluable;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

@gate.annotation.Converter(ExpressionConverter.class)
public class Expression implements Evaluable
{

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

	public static Expression valueOf(String value)
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
		current = index < tokens.size() ? tokens.get(index++) : ExpressionToken.EOF;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws ExpressionException
	{
		try
		{
			writer.write(Renderer.render(evaluate(context, parameters)));
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
		return ternary();
	}

	private Object coalesce() throws ExpressionException
	{
		Object result = or();
		while (current == ExpressionToken.COALESCE)
		{
			next();
			Object other = or();
			if (result == null || result.equals(""))
				result = other;
		}
		return result;
	}

	private Object or() throws ExpressionException
	{
		Object result = and();

		while (current == ExpressionToken.OR)
		{
			next();
			Object object = and();
			result = bool(result) || bool(object);
		}

		return result;
	}

	private Object and() throws ExpressionException
	{
		Object result = sentence();

		while (current == ExpressionToken.AND)
		{
			next();
			Object object = sentence();
			result = bool(result) || bool(object);
		}

		return result;
	}

	private Object sentence() throws ExpressionException
	{
		Object result = comparable();
		while (ExpressionToken.EQ.equals(current)
		       || ExpressionToken.NE.equals(current)
		       || ExpressionToken.GE.equals(current)
		       || ExpressionToken.GT.equals(current)
		       || ExpressionToken.LE.equals(current)
		       || ExpressionToken.LT.equals(current)
		       || ExpressionToken.RX.equals(current)
		       || ExpressionToken.BW.equals(current)
		       || ExpressionToken.IN.equals(current)
		       || ExpressionToken.LK.equals(current)
		       || ExpressionToken.NOT.equals(current))
		{
			ExpressionToken operator = (ExpressionToken) current;
			next();
			result = switch (operator)
			{
				case EQ -> Objects.equals(result, comparable());
				case NE -> !Objects.equals(result, comparable());
				case RX -> rlike(result);
				case BW -> between(result);
				case IN -> tuple().contains(result);
				case LK -> like(result);
				case LT -> ExpressionCalculator.compare(result, comparable()) < 0;
				case GT -> ExpressionCalculator.compare(result, comparable()) > 0;
				case LE -> ExpressionCalculator.compare(result, comparable()) <= 0;
				case GE -> ExpressionCalculator.compare(result, comparable()) >= 0;
				case NOT -> negated(result);
				default -> throw new ExpressionException("Unexpected token \"%s\" found on expression \"%s\".", result, value);
			};
		}
		return result;
	}

	private boolean like(Object result)
	{
		Object object = comparable();
		if (!(result instanceof String s))
			throw new ExpressionException("Expected \"String\" and found \"%s\" on expression \"%s\".", result, value);
		if (!(object instanceof String pattern))
			throw new ExpressionException("Expected \"String\" and found \"%s\" on expression \"%s\".", object, value);
		return s.matches(pattern
				.replace(".", "\\.")
				.replace("%", ".*")
				.replace("_", "."));
	}

	private Object ternary() throws ExpressionException
	{

		Object result = coalesce();
		if (current == ExpressionToken.SHORT_TERNARY)
		{
			next();
			Object otherwise = ternary();
			return bool(result) ? result : otherwise;
		}
		if (current != ExpressionToken.TERNARY)
			return result;
		next();
		Object then = ternary();
		if (current != ExpressionToken.DOUBLE_DOT)
			throw new ExpressionException("Expected \":\" and found \"%s\" on expression \"%s\".", current, value);
		next();
		Object otherwise = ternary();
		return bool(result) ? then : otherwise;
	}

	private Object negated(Object result) throws ExpressionException
	{
		ExpressionToken op = (ExpressionToken) current;
		next();
		return switch (op)
		{
			case IN -> !tuple().contains(result);
			case LK -> !like(result);
			case BW -> !between(result);
			case RX -> !rlike(result);
			default -> throw new ExpressionException(
					"Expected \"in\", \"lk\" or \"bw\" after \"not\" on expression \"%s\".", value);
		};
	}

	private boolean between(Object result)
	{
		Object low = comparable();
		if (current != ExpressionToken.AND)
			throw new ExpressionException("Expected \"and\" and found \"%s\" on expression \"%s\".", current, value);
		next();
		Object high = comparable();
		return ExpressionCalculator.compare(result, low) >= 0
		       && ExpressionCalculator.compare(result, high) <= 0;
	}

	private boolean rlike(Object result)
	{
		Object object = comparable();
		if (result instanceof String && object instanceof String)
			return ((String) result).matches((String) object);
		throw new ExpressionException("Expected \"pattern string\" and found \"%s\" on expression \"%s\".", object, value);
	}

	private Object power() throws ExpressionException
	{
		Object result = not();
		if (current == ExpressionToken.POW)
		{
			next();
			return ExpressionCalculator.pow(result, power());
		}
		return result;
	}

	private Object comparable() throws ExpressionException
	{
		Object result = term();

		while (current == ExpressionToken.ADD || current == ExpressionToken.SUB)
		{
			var operator = (ExpressionToken) current;
			next();
			result = switch (operator)
			{
				case ADD -> ExpressionCalculator.add(result, term());
				case SUB -> ExpressionCalculator.sub(result, term());
				default -> throw new IllegalStateException();
			};
		}

		return result;
	}

	private Object term() throws ExpressionException
	{
		Object result = power();

		while (current == ExpressionToken.MUL
		       || current == ExpressionToken.DIV
		       || current == ExpressionToken.MOD)
		{
			var operator = (ExpressionToken) current;
			next();
			result = switch (operator)
			{
				case MUL -> ExpressionCalculator.mul(result, power());
				case DIV -> ExpressionCalculator.div(result, power());
				case MOD -> ExpressionCalculator.mod(result, power());
				default -> throw new IllegalStateException();
			};
		}

		return result;
	}

	private Object not() throws ExpressionException
	{
		if (ExpressionToken.NOT.equals(current))
		{
			next();
			Object v = not();
			return !bool(v);
		}
		return unary();
	}

	private Set<Object> tuple() throws ExpressionException
	{
		if (current != ExpressionToken.OPEN_PARENTHESES)
			throw new ExpressionException("Expected \"(\" and found \"%s\" on expression \"%s\".", current, value);
		next();
		Set<Object> set = new HashSet<>();
		while (current != ExpressionToken.CLOSE_PARENTHESES)
		{
			if (current == ExpressionToken.EOF)
				throw new ExpressionException("Expected \")\" and found \"%s\" on expression \"%s\".", current, value);
			set.add(comparable());
			if (current == ExpressionToken.COMMA)
				next();
		}
		next();
		return set;
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
		PropertyScanner propertyScanner = new PropertyScanner(name());

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
			string.append(index());

		return string.toString();
	}

	public boolean bool(Object result) throws ExpressionException
	{
		if (result instanceof Boolean b)
			return b;
		throw new ExpressionException("Expected \"Boolean\" and found \"%s\" on expression \"%s\".", result, value);
	}

	private String index() throws ExpressionException
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
		{
			if (ExpressionToken.EOF.equals(current))
				throw new ExpressionException("Expected \")\" and found \"%s\" on expression \"%s\".", current, value);
			string.append(arguments());
		}

		string.append(")");

		next();

		return string.toString();
	}

	private String arguments() throws ExpressionException
	{
		StringBuilder string = new StringBuilder();

		Object value = expression();
		if (value instanceof String)
			string.append("'").append(value).append("'");
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
	public String toString() {return value;}

	@Override
	public boolean equals(Object obj) {return obj instanceof Expression expression && value.equals(expression.value);}

	@Override
	public int hashCode() {return value.hashCode();}
}