package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Expression;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

class TemplateParser
{

	private TemplateToken token;

	public Template parse(Reader reader) throws TemplateException
	{
		try (TemplateScanner scanner = new TemplateScanner(reader))
		{
			return template(scanner);
		} catch (IOException ex)
		{
			throw new TemplateException(ex, "Error trying to parse template.");
		}
	}

	public Template template(TemplateScanner scanner) throws TemplateException
	{
		List<Evaluable> evaluables = new ArrayList<>();

		for (token = scanner.nextTplToken();
			!TemplateToken.Type.EOF.equals(token.getType());
			token = scanner.nextTplToken())
			evaluables.add(evaluable(scanner));
		return new Template(evaluables);
	}

	public Evaluable evaluable(TemplateScanner scanner) throws TemplateException
	{
		switch (token.getType())
		{
			case TEXT:
				return token;
			case EXPRESSION_HEAD:
				return expression(scanner);
			case IF_HEAD:
				return ifTag(scanner);
			case ITERATOR_HEAD:
				return iteratorTag(scanner);
			default:
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		}
	}

	public Expression expression(TemplateScanner scanner) throws TemplateException
	{
		token = scanner.nextExpToken();
		if (!TemplateToken.Type.TEXT.equals(token.getType()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		Expression result = new Expression(token.getValue());

		token = scanner.nextExpToken();
		if (!TemplateToken.Type.EXPRESSION_TAIL.equals(token.getType()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		return result;
	}

	public Expression encolosedExpression(TemplateScanner scanner) throws TemplateException
	{
		token = scanner.nextTagToken();
		switch (token.getType())
		{
			case QUOTE:
			{
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.EXPRESSION_HEAD.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
				Expression result = expression(scanner);
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.QUOTE.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				token = scanner.nextTagToken();
				return result;
			}
			case DOUBLE_QUOTE:
			{
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.EXPRESSION_HEAD.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
				Expression result = expression(scanner);
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.DOUBLE_QUOTE.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				token = scanner.nextTagToken();
				return result;
			}
			default:
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		}
	}

	public String encolosedTarget(TemplateScanner scanner) throws TemplateException
	{
		token = scanner.nextTagToken();
		switch (token.getType())
		{
			case QUOTE:
			{
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.TARGET.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
				String result = token.getValue();

				token = scanner.nextTagToken();
				if (!TemplateToken.Type.QUOTE.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				token = scanner.nextTagToken();
				return result;
			}
			case DOUBLE_QUOTE:
			{
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.TARGET.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
				String result = token.getValue();

				token = scanner.nextTagToken();
				if (!TemplateToken.Type.DOUBLE_QUOTE.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				token = scanner.nextTagToken();
				return result;
			}
			default:
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		}
	}

	public String encolosedIndex(TemplateScanner scanner) throws TemplateException
	{
		token = scanner.nextTagToken();
		switch (token.getType())
		{
			case QUOTE:
			{
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.INDEX.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
				String result = token.getValue();

				token = scanner.nextTagToken();
				if (!TemplateToken.Type.QUOTE.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				token = scanner.nextTagToken();
				return result;
			}
			case DOUBLE_QUOTE:
			{
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.INDEX.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
				String result = token.getValue();

				token = scanner.nextTagToken();
				if (!TemplateToken.Type.DOUBLE_QUOTE.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				token = scanner.nextTagToken();
				return result;
			}
			default:
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		}
	}

	public TemplateIf ifTag(TemplateScanner scanner) throws TemplateException
	{
		token = scanner.nextTagToken();
		if (!TemplateToken.Type.CONDITION.equals(token.getType()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		token = scanner.nextTagToken();
		if (!TemplateToken.Type.EQUALS.equals(token.getType()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		Expression expression = encolosedExpression(scanner);

		if (!TemplateToken.Type.CLOSE_TAG.equals(token.getType()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		Template template = template(scanner);

		token = scanner.nextTagToken();
		if (!TemplateToken.Type.IF_TAIL.equals(token.getType()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		return new TemplateIf(expression, template);
	}

	public TemplateIterator iteratorTag(TemplateScanner scanner) throws TemplateException
	{
		Expression source = null;
		String target = null;
		String index = null;

		token = scanner.nextTagToken();

		while (!TemplateToken.Type.CLOSE_TAG.equals(token.getType()))
		{
			switch (token.getType())
			{
				case SOURCE:

					if (source != null)
						throw new TemplateException("Attempt to specify duplicate target parameter.");

					token = scanner.nextTagToken();
					if (!TemplateToken.Type.EQUALS.equals(token.getType()))
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

					source = encolosedExpression(scanner);

					break;
				case TARGET:

					if (target != null)
						throw new TemplateException("Attempt to specify duplicate source parameter.");

					token = scanner.nextTagToken();
					if (!TemplateToken.Type.EQUALS.equals(token.getType()))
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

					target = encolosedTarget(scanner);
					break;
				case INDEX:

					if (index != null)
						throw new TemplateException("Attempt to specify duplicate index parameter.");

					token = scanner.nextTagToken();
					if (!TemplateToken.Type.EQUALS.equals(token.getType()))
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

					index = encolosedIndex(scanner);

					break;
				case CLOSE_TAG:
					token = scanner.nextTagToken();
					break;

				default:
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			}
		}

		if (source == null)
			throw new TemplateException("Iterator required source parameter not specified.");
		Template template = template(scanner);

		token = scanner.nextTagToken();
		if (!TemplateToken.Type.ITERATOR_TAIL.equals(token.getType()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		return new TemplateIterator(source, target, index, template);
	}

}
