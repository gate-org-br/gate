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
			case IMPORT:
				return importTag(scanner);
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

	public String identifier(TemplateScanner scanner) throws TemplateException
	{
		token = scanner.nextTagToken();
		if (!TemplateToken.Type.IDENTIFIER.equals(token.getType()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		return token.getValue();
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

	public String encolosedIdentifier(TemplateScanner scanner) throws TemplateException
	{
		token = scanner.nextTagToken();
		switch (token.getType())
		{
			case QUOTE:
			{
				token = scanner.nextTagToken();
				if (!TemplateToken.Type.IDENTIFIER.equals(token.getType()))
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
				if (!TemplateToken.Type.IDENTIFIER.equals(token.getType()))
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

	public String className(TemplateScanner scanner)
	{
		StringBuilder string = new StringBuilder();
		while (true)
		{

			token = scanner.nextTagToken();
			if (!TemplateToken.Type.IDENTIFIER.equals(token.getType()))
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			string.append(token.getValue());

			token = scanner.nextTagToken();
			if (token.getType() != TemplateToken.Type.DOT)
				break;
			string.append(token.getValue());
		}

		return string.toString();
	}

	public String encolosedClassName(TemplateScanner scanner) throws TemplateException
	{
		token = scanner.nextTagToken();
		switch (token.getType())
		{
			case QUOTE:
			{
				String result = className(scanner);

				if (!TemplateToken.Type.QUOTE.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				token = scanner.nextTagToken();
				return result;
			}
			case DOUBLE_QUOTE:
			{
				String result = className(scanner);

				if (!TemplateToken.Type.DOUBLE_QUOTE.equals(token.getType()))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				token = scanner.nextTagToken();
				return result;
			}
			default:
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		}
	}

	public String encolosedUrl(TemplateScanner scanner) throws TemplateException
	{
		StringBuilder url = new StringBuilder();

		token = scanner.nextTagToken();
		switch (token.getType())
		{
			case QUOTE:
			{
				token = scanner.nextTagToken();
				while (token.getType() != TemplateToken.Type.QUOTE)
				{
					if (token.getType() == TemplateToken.Type.EOF)
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

					url.append(token.getValue());
					token = scanner.nextTagToken();
				}

				return url.toString();
			}
			case DOUBLE_QUOTE:
			{
				token = scanner.nextTagToken();
				while (token.getType() != TemplateToken.Type.DOUBLE_QUOTE)
				{
					if (token.getType() == TemplateToken.Type.EOF)
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

					url.append(token.getValue());
					token = scanner.nextTagToken();
				}

				return url.toString();
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

	public TemplateImport importTag(TemplateScanner scanner) throws TemplateException
	{
		try
		{
			String type = null;
			String resource = null;

			token = scanner.nextTagToken();

			while (!TemplateToken.Type.SELF_CLOSE_TAG.equals(token.getType()))
			{
				switch (token.getType())
				{
					case TYPE:

						if (type != null)
							throw new TemplateException("Attempt to specify duplicate type parameter.");

						token = scanner.nextTagToken();
						if (!TemplateToken.Type.EQUALS.equals(token.getType()))
							throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

						type = encolosedClassName(scanner);

						break;
					case RESOURCE:

						if (resource != null)
							throw new TemplateException("Attempt to specify duplicate resource parameter.");

						token = scanner.nextTagToken();
						if (!TemplateToken.Type.EQUALS.equals(token.getType()))
							throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

						resource = encolosedUrl(scanner);
						token = scanner.nextTagToken();
						break;

					default:
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
				}
			}

			if (type == null)
				throw new TemplateException("Iterator required type parameter not specified.");
			if (resource == null)
				throw new TemplateException("Iterator required resource parameter not specified.");

			return new TemplateImport(Template.compile(Class.forName(type).getResource(resource)));
		} catch (ClassNotFoundException ex)
		{
			throw new TemplateException(ex.getMessage(), ex);
		}
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

					target = encolosedIdentifier(scanner);
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
