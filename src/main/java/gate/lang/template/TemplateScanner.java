package gate.lang.template;

import gate.error.TemplateException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

class TemplateScanner
{

	private int index;
	private List<Evaluable> tokens;

	private TemplateScanner(List<Evaluable> tokens)
	{
		this.index = 0;
		this.tokens = tokens;

	}

	public static TemplateScanner parse(Reader reader)
	{
		try
		{
			int c = reader.read();
			List<Evaluable> tokens = new ArrayList<>();
			while (c != -1)
			{
				switch (c)
				{
					case '\n' ->
					{
						c = reader.read();
						tokens.add(TemplateToken.LINE_BREAK);
					}
					case ' ', '\t' ->
					{
						StringBuilder token = new StringBuilder();
						token.append((char) c);
						for (c = reader.read(); c == ' '
								|| c == '\t'; c = reader.read())
							token.append((char) c);
						tokens.add(new Spacing(token.toString()));
					}
					case '{' ->
					{
						c = reader.read();
						if (c == '{')
						{
							c = reader.read();
							switch (c)
							{
								case '#' ->
								{
									c = reader.read();
									tokens.add(TemplateToken.BLOCK);
								}
								case '^' ->
								{
									c = reader.read();
									tokens.add(TemplateToken.INVERTED_BLOCK);
								}
								case '/' ->
								{
									c = reader.read();
									tokens.add(TemplateToken.BLOCK_END);
								}
								default ->
									tokens.add(TemplateToken.EXPRESSION_HEAD);
							}
						} else
							tokens.add(new Char('{'));
					}
					case '}' ->
					{
						c = reader.read();
						if (c == '}')
						{
							c = reader.read();
							tokens.add(TemplateToken.EXPRESSION_TAIL);
						} else
							tokens.add(TemplateToken.EL_TAIL);
					}

					case '$' ->
					{
						c = reader.read();
						if (c == '{')
						{
							c = reader.read();
							tokens.add(TemplateToken.EL_HEAD);
						} else
							tokens.add(new Char('$'));
					}

					default ->
					{
						StringBuilder token = new StringBuilder();
						token.append((char) c);
						for (c = reader.read();
								c != -1
								&& c != '{'
								&& c != '}'
								&& c != ' '
								&& c != '\n';
								c = reader.read())
							token.append((char) c);
						tokens.add(new Text(token.toString()));
					}
				}

			}

			tokens.add(TemplateToken.EOF);

			return new TemplateScanner(tokens);

		} catch (IOException ex)
		{
			throw new TemplateException(ex, "Error trying to parse template.");
		}
	}

	public Evaluable next()
	{
		var result = tokens.get(index);
		if (result != TemplateToken.EOF)
			index++;
		return result;
	}

	public Evaluable peek(int n)
	{
		if (tokens.size() > index + n)
			return tokens.get(index + n);
		return TemplateToken.EOF;
	}
}
