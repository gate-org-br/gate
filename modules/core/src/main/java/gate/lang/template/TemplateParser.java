package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Expression;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

class TemplateParser
{

	private Evaluable token;

	public Template parse(Reader reader) throws TemplateException
	{
		TemplateScanner scanner = TemplateScanner.parse(reader);
		token = scanner.next();
		return template(scanner);
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
		if (token == TemplateToken.LINE_BREAK
				&& scanner.peek(0) instanceof Spacing
				&& (scanner.peek(1) == TemplateToken.BLOCK
				|| scanner.peek(1) == TemplateToken.INVERTED_BLOCK
				|| scanner.peek(1) == TemplateToken.BLOCK_END))
		{
			token = scanner.next();
			token = scanner.next();
			return TemplateToken.LINE_BREAK;
		}

		if (token == TemplateToken.EXPRESSION_HEAD
				|| token == TemplateToken.EL_HEAD)
			return expression(scanner);

		if (token == TemplateToken.BLOCK)
			return block(scanner);

		if (token == TemplateToken.INVERTED_BLOCK)
			return invertedBlock(scanner);

		return text(scanner);
	}

	public void skipIdentation(TemplateScanner scanner)
	{
		if (token == TemplateToken.LINE_BREAK)
		{
			token = scanner.next();
		} else if (token instanceof Spacing
				&& scanner.peek(0) == TemplateToken.LINE_BREAK)
		{
			token = scanner.next();
			token = scanner.next();
		}
	}

	public Evaluable block(TemplateScanner scanner)
	{
		var expression = expression(scanner);
		skipIdentation(scanner);

		var template = template(scanner);

		if (token != TemplateToken.BLOCK_END)
			throw new TemplateException(String.format("Expected %s and found  %s.", TemplateToken.BLOCK_END, token));

		if (!expression.equals(expression(scanner)))
			throw new TemplateException("Invalid block end");
		skipIdentation(scanner);
		return new Block(expression, template);
	}

	public Evaluable invertedBlock(TemplateScanner scanner)
	{
		var expression = expression(scanner);
		skipIdentation(scanner);

		var template = template(scanner);

		if (token == TemplateToken.EOF)
			throw new TemplateException(String.format("Expected %s and found  %s.", TemplateToken.BLOCK_END, token));

		if (!expression.equals(expression(scanner)))
			throw new TemplateException("Invalid block end");
		skipIdentation(scanner);
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
