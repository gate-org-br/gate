package gate.lang.expression;

import gate.error.ExpressionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

final class ExpressionScanner extends BufferedReader
{

	private int c;
	private final String value;

	public ExpressionScanner(String value) throws ExpressionException
	{
		super(new StringReader(value));
		this.value = value;
		try
		{
			c = read();
		} catch (IOException e)
		{
			throw new ExpressionException("Error trying to evaluate expression: %s.", value);
		}
	}

	public Object next() throws ExpressionException
	{
		try
		{
			while (Character.isWhitespace(c))
				c = read();
			if (c == -1)
				return ExpressionToken.EOF;
			switch (c)
			{
				case '@':
					c = read();
					return ExpressionToken.VARIABLE;
				case '^':
					c = read();
					return ExpressionToken.CONTEXT;
				case ',':
					c = read();
					return ExpressionToken.COMMA;
				case '[':
					c = read();
					return ExpressionToken.OPEN_BRACKET;
				case ']':
					c = read();
					return ExpressionToken.CLOSE_BRACKET;
				case '.':
					c = read();
					return ExpressionToken.DOT;
				case '(':
					c = read();
					return ExpressionToken.OPEN_PARENTHESES;
				case ')':
					c = read();
					return ExpressionToken.CLOSE_PARENTHESES;
				case '+':
					c = read();
					return ExpressionToken.ADD;
				case '-':
					c = read();
					return ExpressionToken.SUB;
				case '*':
					c = read();
					return ExpressionToken.MUL;
				case '/':
					c = read();
					return ExpressionToken.DIV;
				case '%':
					c = read();
					return ExpressionToken.MOD;
				case '=':
					c = read();
					if (c != '=')
						throw new ExpressionException("Unexpected token \"=%c\" on expression \"%s\".", (char) c, value);
					c = read();
					return ExpressionToken.EQ;
				case '&':
					c = read();
					if (c != '&')
						throw new ExpressionException("Unexpected token \"&%c\" on expression \"%s\".", (char) c, value);
					c = read();
					return ExpressionToken.AND;
				case '|':
					c = read();
					if (c != '|')
						throw new ExpressionException("Unexpected token \"|%c\" on expression \"%s\".", (char) c, value);
					c = read();
					return ExpressionToken.OR;
				case '!':
					c = read();
					if (c == '=')
					{
						c = read();
						return ExpressionToken.NE;
					} else
						return ExpressionToken.NOT;
				case '>':
					c = read();
					if (c == '=')
					{
						c = read();
						return ExpressionToken.GE;
					} else
						return ExpressionToken.GT;
				case '<':
					c = read();
					if (c == '=')
					{
						c = read();
						return ExpressionToken.LE;
					} else
						return ExpressionToken.LT;

				case '"':
					StringBuilder dqstring = new StringBuilder();
					for (c = read(); c != '"'; c = read())
						dqstring.append((char) c);
					c = read();
					return dqstring.toString();
				case '\'':
					StringBuilder sqstring = new StringBuilder();
					for (c = read(); c != '\''; c = read())
						sqstring.append((char) c);
					c = read();
					return sqstring.toString();

				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					StringBuilder number = new StringBuilder();
					do
						number.append((char) c);
					while (Character.isDigit(c = read()));

					if (c == '.')
					{
						number.append((char) c);
						if (!Character.isDigit(c = read()))
							throw new ExpressionException("Expeted digit and found \"%c\" on expression \"%s\".", (char) c, value);
						do
							number.append((char) c);
						while (Character.isDigit(c = read()));
						return Double.valueOf(number.toString());
					}
					return Integer.valueOf(number.toString());

				default:

					if (!Character.isJavaIdentifierStart(c))
						throw new ExpressionException("Expected property name and found %c", c);
					StringBuilder identifier = new StringBuilder();

					do
						identifier.append((char) c);
					while (Character.isJavaIdentifierPart(c = read()));

					String name = identifier.toString();

					switch (name)
					{
						case "eq":
							return ExpressionToken.EQ;
						case "ne":
							return ExpressionToken.NE;
						case "ge":
							return ExpressionToken.GE;
						case "gt":
							return ExpressionToken.GT;
						case "le":
							return ExpressionToken.LE;
						case "lt":
							return ExpressionToken.LT;
						case "rx":
							return ExpressionToken.RX;
						case "or":
							return ExpressionToken.OR;
						case "and":
							return ExpressionToken.AND;
						case "not":
							return ExpressionToken.NOT;
						case "empty":
							return ExpressionToken.EMPTY;
						case "size":
							return ExpressionToken.SIZE;
						case "true":
							return Boolean.TRUE;
						case "false":
							return Boolean.FALSE;
					}

					return identifier;
			}
		} catch (IOException e)
		{
			throw new ExpressionException("Error trying to evaluate expression: %s.", value);
		}
	}
}
