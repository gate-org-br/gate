package gate.lang.HTMLCleaner;

import gate.error.TemplateException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

class Parser
{

	private Object token;

	public HTMLCleaner parse(Reader reader) throws TemplateException
	{
		try (Scanner scanner = new Scanner(reader))
		{
			token = scanner.next();
			return next(scanner);
		} catch (IOException ex)
		{
			throw new TemplateException(ex, "Error trying to parse template.");
		}
	}

	public HTMLCleaner next(Scanner scanner) throws TemplateException
	{
		List<Evaluable> evaluables = new ArrayList<>();

		while (token != Token.EOF)
			evaluables.add(evaluable(scanner));

		return new HTMLCleaner(evaluables);
	}

	public Evaluable evaluable(Scanner scanner) throws TemplateException
	{
		if (token == Token.OPEN_TAG)
			return tag(scanner);
		else if (token instanceof Token)
			return escape(scanner);
		else
			return text(scanner);
	}

	public Evaluable escape(Scanner scanner) throws TemplateException
	{
		Object value = token;
		token = scanner.next();
		return new Char(((Token) value).toString().charAt(0));
	}

	public Evaluable text(Scanner scanner) throws TemplateException
	{
		Evaluable evaluable = (Char) token;
		token = scanner.next();
		return evaluable;
	}

	public Evaluable tag(Scanner scanner) throws TemplateException
	{
		token = scanner.next();
		while (token != Token.EOF
			&& token != Token.CLOSE_TAG)
		{
			if (token == Token.QUOTE
				|| token == Token.DOUBLE_QUOTE)
				string(scanner);
			else
				token = scanner.next();
		}
		token = scanner.next();

		return VoidEvaluable.INSTANCE;
	}

	public void string(Scanner scanner) throws TemplateException
	{
		if (token == Token.QUOTE)
		{
			do
			{
				token = scanner.next();
			} while (token != Token.QUOTE
				&& token != Token.EOF);

			if (token != Token.QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			token = scanner.next();
		} else if (token == Token.DOUBLE_QUOTE)
		{
			StringBuilder result = new StringBuilder();
			do
			{
				result.append(token.toString());
				token = scanner.next();
			} while (token != Token.DOUBLE_QUOTE
				&& token != Token.EOF);

			if (token != Token.DOUBLE_QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			token = scanner.next();
		} else
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
	}

}
