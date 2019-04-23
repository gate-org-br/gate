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
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to parse template.");
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
			case FOR_HEAD:
				return forTag(scanner);
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

	public String encolosedName(TemplateScanner scanner) throws TemplateException
	{
		token = scanner.nextTagToken();
		switch (token.getType())
		{
			case QUOTE:
			{
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.NAME.equals(token.getType()))
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
				if (!TemplateToken.Type.NAME.equals(token.getType()))
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

	public TemplateFor forTag(TemplateScanner scanner) throws TemplateException
	{
		String name = null;
		Expression each = null;

		token = scanner.nextTagToken();

		while (!TemplateToken.Type.CLOSE_TAG.equals(token.getType()))
		{
			switch (token.getType())
			{
				case EACH:

					if (each != null)
						throw new TemplateException("Attempt to specify more than one each parameter.");

					token = scanner.nextTagToken();
					if (!TemplateToken.Type.EQUALS.equals(token.getType()))
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

					each = encolosedExpression(scanner);

					break;
				case NAME:

					if (name != null)
						throw new TemplateException("Attempt to specify more than one name parameter.");

					token = scanner.nextTagToken();
					if (!TemplateToken.Type.EQUALS.equals(token.getType()))
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

					name = encolosedName(scanner);
					break;
				case CLOSE_TAG:
					token = scanner.nextTagToken();
					break;

				default:
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			}
		}

		if (each == null)
			throw new TemplateException("Tag for required each parameter not specified.");

		Template template = template(scanner);

		token = scanner.nextTagToken();
		if (!TemplateToken.Type.FOR_TAIL.equals(token.getType()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		return new TemplateFor(each, name, template);
	}

}
