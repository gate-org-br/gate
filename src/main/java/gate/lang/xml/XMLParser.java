package gate.lang.xml;

import gate.error.TemplateException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

class XMLParser
{

	private XMLEvaluable token;

	public List<XMLEvaluable> parse(Reader reader) throws TemplateException
	{
		try (XMLScanner scanner = new XMLScanner(reader))
		{
			token = scanner.next();
			return parse(scanner);
		} catch (IOException ex)
		{
			throw new TemplateException(ex, "Error trying to parse template.");
		}
	}

	public List<XMLEvaluable> parse(XMLScanner scanner) throws TemplateException
	{
		List<XMLEvaluable> evaluables = new ArrayList<>();

		while (token != XMLToken.EOF)
			evaluables.add(evaluable(scanner));

		return evaluables;
	}

	public XMLEvaluable evaluable(XMLScanner scanner) throws TemplateException
	{
		switch (token.getType())
		{
			case CHAR:
				return next(scanner);
			case ENTITY:
				return next(scanner);
			case VOID:
				return next(scanner);
			case TOKEN:
				if (token == XMLToken.OPEN_TAG)
					return tag(scanner);
				else if (token instanceof XMLToken)
					return escape(scanner);
			default:
				throw new TemplateException("Invalid token type");
		}
	}

	public XMLEvaluable escape(XMLScanner scanner) throws TemplateException
	{
		Object value = token;
		token = scanner.next();
		return XMLChar.of(((XMLToken) value).toString().charAt(0));
	}

	public XMLEvaluable next(XMLScanner scanner) throws TemplateException
	{
		XMLEvaluable evaluable = token;
		token = scanner.next();
		return evaluable;
	}

	public XMLEvaluable tag(XMLScanner scanner) throws TemplateException
	{
		token = scanner.next();
		while (token != XMLToken.EOF
			&& token != XMLToken.CLOSE_TAG)
		{
			if (token == XMLToken.QUOTE
				|| token == XMLToken.DOUBLE_QUOTE)
				string(scanner);
			else
				token = scanner.next();
		}
		token = scanner.next();

		return VoidXMLEvaluable.INSTANCE;
	}

	public void string(XMLScanner scanner) throws TemplateException
	{
		if (token == XMLToken.QUOTE)
		{
			do
			{
				token = scanner.next();
			} while (token != XMLToken.QUOTE
				&& token != XMLToken.EOF);

			if (token != XMLToken.QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			token = scanner.next();
		} else if (token == XMLToken.DOUBLE_QUOTE)
		{
			StringBuilder result = new StringBuilder();
			do
			{
				result.append(token.toString());
				token = scanner.next();
			} while (token != XMLToken.DOUBLE_QUOTE
				&& token != XMLToken.EOF);

			if (token != XMLToken.DOUBLE_QUOTE)
				throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
			token = scanner.next();
		} else
			throw new TemplateException(String.format("Unexpeted token: %s.", token.toString()));
	}

}
