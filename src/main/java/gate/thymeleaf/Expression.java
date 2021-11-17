package gate.thymeleaf;

import gate.converter.Converter;
import java.util.regex.Pattern;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

public class Expression
{

	private static final Pattern VAR = Pattern.compile("^\\$\\{[^}]+\\}$");

	private final ITemplateContext context;
	private final IStandardExpressionParser parser;

	public static Expression of(ITemplateContext context)
	{
		return new Expression(context);
	}

	private Expression(ITemplateContext context)
	{
		this.context = context;
		parser = StandardExpressions.getExpressionParser(context.getConfiguration());
	}

	public Object evaluate(String expression)
	{
		if (expression == null)
			return null;
		if (!expression.contains("${"))
			return expression;
		if (VAR.matcher(expression).matches())
			return parser.parseExpression(context, expression).execute(context);

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < expression.length(); i++)
			if (expression.charAt(i) == '$')
			{
				i++;
				if (expression.charAt(i) == '{')
				{
					StringBuilder token = new StringBuilder("$");

					do
					{
						token.append(expression.charAt(i++));
					} while (i < expression.length()
						&& expression.charAt(i) != '}');

					token.append('}');

					result.append(Converter.toText(parser.parseExpression(context,
						token.toString()).execute(context)));
				} else
					result.append('$').append(expression.charAt(i));
			} else
				result.append(expression.charAt(i));
		return result.toString();
	}
}
