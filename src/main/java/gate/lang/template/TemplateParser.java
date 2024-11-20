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
			token = scanner.next();
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
				&& token != TemplateToken.BLOCK_END)
			evaluables.add(evaluable(scanner));
		return new Template(evaluables);
	}

	public Evaluable evaluable(TemplateScanner scanner) throws TemplateException
	{
		if (token == TemplateToken.EXPRESSION_HEAD
				|| token == TemplateToken.EL_HEAD)
			return expression(scanner);

		if (token == TemplateToken.BLOCK)
			return block(scanner);

		if (token == TemplateToken.INVERTED_BLOCK)
			return invertedBlock(scanner);

		return text(scanner);
	}

	public Evaluable block(TemplateScanner scanner)
	{
		var expression = expression(scanner);

		var template = template(scanner);

		if (token != TemplateToken.BLOCK_END)
			throw new TemplateException(String.format("Expected %s and found  %s.", TemplateToken.BLOCK_END, token));

		if (!expression.equals(expression(scanner)))
			throw new TemplateException("Invalid block end");

		return new Block(expression, template);
	}

	public Evaluable invertedBlock(TemplateScanner scanner)
	{
		var expression = expression(scanner);

		var template = template(scanner);

		if (token == TemplateToken.EOF)
			throw new TemplateException(String.format("Expected %s and found  %s.", TemplateToken.BLOCK_END, token));

		if (!expression.equals(expression(scanner)))
			throw new TemplateException("Invalid block end");

		return new InvertedBlock(expression, template);
	}

	public Evaluable text(TemplateScanner scanner) throws TemplateException
	{
		Evaluable evaluable = token;
		token = scanner.next();
		return evaluable;
	}

	public Expression expression(TemplateScanner scanner) throws TemplateException
	{
		if (token == TemplateToken.EXPRESSION_HEAD
				|| token == TemplateToken.BLOCK
				|| token == TemplateToken.INVERTED_BLOCK
				|| token == TemplateToken.BLOCK_END)
		{
			StringBuilder string = new StringBuilder();
			for (token = scanner.next();
					token != TemplateToken.EXPRESSION_TAIL
					&& token != TemplateToken.EOF;
					token = scanner.next())
				string.append(token.toString());

			if (token == TemplateToken.EOF)
				throw new TemplateException(String.format("Expected %s and found  %s.", TemplateToken.EXPRESSION_TAIL, token));
			token = scanner.next();
			return Expression.of(string.toString());
		} else if (token == TemplateToken.EL_HEAD)
		{
			StringBuilder string = new StringBuilder();
			for (token = scanner.next();
					token != TemplateToken.EXPRESSION_TAIL
					&& token != TemplateToken.EOF;
					token = scanner.next())
				string.append(token.toString());

			if (token != TemplateToken.EOF)
				throw new TemplateException(String.format("Expected %s and found  %s.", TemplateToken.EXPRESSION_TAIL, token));
			token = scanner.next();
			return Expression.of(string.toString());
		} else
			throw new TemplateException(String.format("Expected expression and found  %s.", token));
	}
}
