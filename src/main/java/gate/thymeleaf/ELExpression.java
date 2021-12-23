package gate.thymeleaf;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.LambdaExpression;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public interface ELExpression
{

	public Object evaluate(String expression);

	public Function<Object, Object> function(String expression);

	public Comparator<Object> comparator(String expression);

	@RequestScoped
	public static class ELExpressionImpl implements ELExpression
	{

		private final ELContext context;
		private final ExpressionFactory factory;
		private static final Pattern VAR = Pattern.compile("^\\$\\{[^}]+\\}$");
		private static final Pattern FUNCTION = Pattern.compile("^\\$\\{([a-zA-Z][a-zA-Z0-9]*) *-> *(.*)\\}$");
		private static final Pattern BI_FUNCTION = Pattern.compile("^\\$\\{\\(([a-zA-Z][a-zA-Z0-9]*), *([a-zA-Z][a-zA-Z0-9]*)\\)\\ *-> *(.*)}$");

		@Inject
		public ELExpressionImpl(BeanManager beanManager, HttpServletRequest request)
		{

			this.factory = ExpressionFactory.newInstance();
			context = new JavaELContext(factory, beanManager, request);

		}

		@Override
		public Object evaluate(String expression)
		{
			if (expression == null)
				return null;
			if (!expression.contains("${"))
				return expression;
			if (VAR.matcher(expression).matches())
				return factory.createValueExpression(context, expression, Object.class).getValue(context);

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

						var value = factory.createValueExpression(context, token.toString(), Object.class)
							.getValue(context);
						result.append(value != null ? value.toString() : null);
					} else
						result.append('$').append(expression.charAt(i));
				} else
					result.append(expression.charAt(i));
			return result.toString();
		}

		@Override
		public Function<Object, Object> function(String expression)
		{
			Matcher matcher = FUNCTION.matcher(expression);
			if (!matcher.matches())
				throw new ELException(expression + " is not a valid lambda function");

			var lambda = new LambdaExpression(List.of(matcher.group(1)),
				factory.createValueExpression(context, "${" + matcher.group(2) + "}",
					Object.class));

			return a -> lambda.invoke(context, a);
		}

		@Override
		public Comparator<Object> comparator(String expression)
		{
			Matcher matcher = BI_FUNCTION.matcher(expression);
			if (!matcher.matches())
				throw new ELException(expression + " is not a valid lambda comparator");

			var lambda = new LambdaExpression(List.of(matcher.group(1), matcher.group(2)),
				factory.createValueExpression(context, "${" + matcher.group(3) + "}",
					Object.class));

			return (a, b) -> (int) lambda.invoke(context, a, b);
		}

	}
}
