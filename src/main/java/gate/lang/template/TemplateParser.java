package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Expression;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

class TemplateParser
{

	private Evaluable token;

	public Template parse(Reader reader) throws TemplateException
	{
		try (TemplateScanner scanner = new TemplateScanner(reader))
		{
			token = scanner.nextTagToken();
			return template(scanner);
		} catch (IOException ex)
		{
			throw new TemplateException(ex, "Error trying to parse template.");
		}
	}

	public Template template(TemplateScanner scanner) throws TemplateException
	{
		List<Evaluable> evaluables = new ArrayList<>();

		while (token != TemplateToken.EOF
			&& token != TemplateToken.IF_TAIL
			&& token != TemplateToken.ITERATOR_TAIL)
			evaluables.add(evaluable(scanner));

		return new Template(evaluables);
	}

	public Evaluable evaluable(TemplateScanner scanner) throws TemplateException
	{
		if (token == TemplateToken.EXPRESSION_HEAD)
			return expression(scanner);
		else if (token == TemplateToken.IF_HEAD)
			return ifTag(scanner);
		else if (token == TemplateToken.ITERATOR_HEAD)
			return iteratorTag(scanner);
		else if (token == TemplateToken.IMPORT)
			return importTag(scanner);
		else if (token == TemplateToken.LINE_BREAK
			|| token == TemplateToken.WINDOWS_LINE_BREAK)
			return identation(scanner);
		else
			return text(scanner);
	}

	public Evaluable text(TemplateScanner scanner) throws TemplateException
	{
		Evaluable evaluable = token;
		token = scanner.nextTagToken();
		return evaluable;
	}

	public Evaluable identation(TemplateScanner scanner)
	{
		StringBuilder string
			= new StringBuilder(token.toString());

		token = scanner.nextTagToken();
		while (token == TemplateToken.SPACE
			|| token == TemplateToken.TAB)
		{
			string.append(token.toString());
			token = scanner.nextTagToken();
		}

		if (token == TemplateToken.IF_HEAD)
			return ifTag(scanner);
		else if (token == TemplateToken.ITERATOR_HEAD)
			return iteratorTag(scanner);
		else if (token == TemplateToken.IF_TAIL
			|| token == TemplateToken.ITERATOR_TAIL)
			return None.INSTANCE;
		else if (token == TemplateToken.IMPORT)
			return importTag(scanner);
		else
			return new Identation(string.toString());
	}

	public String string(TemplateScanner scanner) throws TemplateException
	{
		if (token == TemplateToken.QUOTE)
		{
			StringBuilder result = new StringBuilder();
			do
			{
				result.append(token.toString());
				token = scanner.nextTagToken();
			} while (token != TemplateToken.QUOTE
				&& token != TemplateToken.EOF);

			if (token != TemplateToken.QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			result.append(token.toString());
			token = scanner.nextTagToken();
			return result.toString();
		} else if (token == TemplateToken.DOUBLE_QUOTE)
		{
			StringBuilder result = new StringBuilder();
			do
			{
				result.append(token.toString());
				token = scanner.nextTagToken();
			} while (token != TemplateToken.DOUBLE_QUOTE
				&& token != TemplateToken.EOF);

			if (token != TemplateToken.DOUBLE_QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			result.append(token.toString());
			token = scanner.nextTagToken();
			return result.toString();
		} else
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
	}

	public Expression expression(TemplateScanner scanner) throws TemplateException
	{
		if (token != TemplateToken.EXPRESSION_HEAD)
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		token = scanner.nextTagToken();

		StringBuilder string = new StringBuilder();
		while (token != TemplateToken.EXPRESSION_TAIL
			&& token != TemplateToken.EOF)
		{
			if (token == TemplateToken.QUOTE
				|| token == TemplateToken.DOUBLE_QUOTE)
				string.append(string(scanner));
			else
				string.append(token.toString());
			token = scanner.nextTagToken();
		}

		if (token != TemplateToken.EXPRESSION_TAIL)
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		token = scanner.nextTagToken();

		return new Expression(string.toString());
	}

	public Expression encolosedExpression(TemplateScanner scanner) throws TemplateException
	{
		nextTagToken(scanner);
		if (token == TemplateToken.QUOTE)
		{
			nextTagToken(scanner);

			Expression result = expression(scanner);

			if (token != TemplateToken.QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

			nextTagToken(scanner);
			return result;
		} else if (token == TemplateToken.DOUBLE_QUOTE)
		{
			nextTagToken(scanner);

			Expression result = expression(scanner);

			if (token != TemplateToken.DOUBLE_QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

			nextTagToken(scanner);
			return result;
		} else
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
	}

	public String identifier(TemplateScanner scanner) throws TemplateException
	{
		if (!(token instanceof Char)
			|| !Character.isJavaIdentifierStart(((Char) token).getValue()))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		StringBuilder string = new StringBuilder();
		while (token instanceof Char
			&& Character.isJavaIdentifierPart(((Char) token).getValue()))
		{
			string.append(token.toString().charAt(0));
			nextTagToken(scanner);
		}
		return string.toString();
	}

	public String encolosedIdentifier(TemplateScanner scanner) throws TemplateException
	{
		nextTagToken(scanner);
		if (token == TemplateToken.QUOTE)
		{
			nextTagToken(scanner);

			String result = identifier(scanner);

			if (token != TemplateToken.QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

			nextTagToken(scanner);
			return result;
		} else if (token == TemplateToken.DOUBLE_QUOTE)
		{
			nextTagToken(scanner);

			String result = identifier(scanner);

			if (token != TemplateToken.DOUBLE_QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

			nextTagToken(scanner);
			return result;
		} else
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
	}

	public String className(TemplateScanner scanner)
	{
		StringBuilder string
			= new StringBuilder(identifier(scanner));
		while (token == TemplateToken.DOT)
		{
			string.append(token.toString());
			nextTagToken(scanner);
			string.append(identifier(scanner));
		}

		return string.toString();
	}

	private void nextTagToken(TemplateScanner scanner) throws TemplateException
	{
		scanner.skipSpaces();
		token = scanner.nextTagToken();
	}

	public String encolosedClassName(TemplateScanner scanner) throws TemplateException
	{
		nextTagToken(scanner);
		if (token == TemplateToken.QUOTE)
		{
			nextTagToken(scanner);
			String result = className(scanner);

			if (token != TemplateToken.QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

			nextTagToken(scanner);
			return result;
		} else if (token == TemplateToken.DOUBLE_QUOTE)
		{
			nextTagToken(scanner);
			String result = className(scanner);

			if (token != TemplateToken.DOUBLE_QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

			nextTagToken(scanner);
			return result;
		} else
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
	}

	public String encolosedText(TemplateScanner scanner) throws TemplateException
	{
		StringBuilder url = new StringBuilder();

		nextTagToken(scanner);
		if (token == TemplateToken.QUOTE)
		{
			nextTagToken(scanner);
			while (token != TemplateToken.QUOTE)
			{
				if (token == TemplateToken.EOF)
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				url.append(token.toString());
				nextTagToken(scanner);
			}

			return url.toString();
		} else if (token == TemplateToken.DOUBLE_QUOTE)
		{
			nextTagToken(scanner);
			while (token != TemplateToken.DOUBLE_QUOTE)
			{
				if (token == TemplateToken.EOF)
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				url.append(token.toString());
				nextTagToken(scanner);
			}

			return url.toString();
		} else
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
	}

	public TemplateIf ifTag(TemplateScanner scanner) throws TemplateException
	{
		nextTagToken(scanner);
		if (!TemplateToken.CONDITION.equals(token))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		nextTagToken(scanner);
		if (!TemplateToken.EQUALS.equals(token))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

		Expression expression = encolosedExpression(scanner);

		if (!TemplateToken.CLOSE_TAG.equals(token))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		token = scanner.nextTagToken();

		Template template = template(scanner);

		if (!TemplateToken.IF_TAIL.equals(token))
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		token = scanner.nextTagToken();

		return new TemplateIf(expression, template);
	}

	public TemplateImport importTag(TemplateScanner scanner) throws TemplateException
	{
		try
		{
			String type = null;
			String resource = null;

			nextTagToken(scanner);

			while (token != TemplateToken.SELF_CLOSE_TAG
				&& token != TemplateToken.EOF)
			{
				if (token == TemplateToken.TYPE)
				{
					if (type != null)
						throw new TemplateException("Attempt to specify duplicate type parameter.");

					nextTagToken(scanner);
					if (!TemplateToken.EQUALS.equals(token))
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

					type = encolosedClassName(scanner);
				} else if (token == TemplateToken.RESOURCE)
				{
					if (resource != null)
						throw new TemplateException("Attempt to specify duplicate resource parameter.");

					nextTagToken(scanner);
					if (!TemplateToken.EQUALS.equals(token))
						throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

					resource = encolosedText(scanner);
					nextTagToken(scanner);
				} else
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			}

			if (token != TemplateToken.SELF_CLOSE_TAG)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			token = scanner.nextTagToken();

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

		nextTagToken(scanner);

		while (!TemplateToken.CLOSE_TAG.equals(token)
			&& token != TemplateToken.EOF)
		{
			if (token == TemplateToken.SOURCE)
			{
				if (source != null)
					throw new TemplateException("Attempt to specify duplicate target parameter.");

				nextTagToken(scanner);
				if (!TemplateToken.EQUALS.equals(token))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				source = encolosedExpression(scanner);

			} else if (token == TemplateToken.TARGET)
			{
				if (target != null)
					throw new TemplateException("Attempt to specify duplicate source parameter.");

				nextTagToken(scanner);
				if (!TemplateToken.EQUALS.equals(token))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				target = encolosedIdentifier(scanner);
			} else if (token == TemplateToken.INDEX)
			{
				if (index != null)
					throw new TemplateException("Attempt to specify duplicate index parameter.");

				nextTagToken(scanner);
				if (!TemplateToken.EQUALS.equals(token))
					throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));

				index = encolosedIdentifier(scanner);

				break;
			} else
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		}

		if (token != TemplateToken.CLOSE_TAG)
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		token = scanner.nextTagToken();

		if (source == null)
			throw new TemplateException("Iterator required source parameter not specified.");

		Template template = template(scanner);

		if (token != TemplateToken.ITERATOR_TAIL)
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
		token = scanner.nextTagToken();

		return new TemplateIterator(source, target, index, template);
	}

}
