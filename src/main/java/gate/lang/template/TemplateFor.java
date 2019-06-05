package gate.lang.template;

import gate.error.ExpressionException;
import gate.error.TemplateException;
import gate.lang.expression.Expression;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class TemplateFor implements Evaluable
{

	private final Expression expression;
	private final Template template;
	private final String name;

	public TemplateFor(Expression expression, String name, Template template)
	{
		this.expression = expression;
		this.template = template;
		this.name = name;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Map<String, Object> parameters) throws TemplateException
	{
		try
		{
			Object values = expression
				.evaluate(context, parameters);

			Iterable<?> iterable;
			if (values instanceof Collection<?>)
				iterable = (Collection<?>) values;
			else if (values instanceof Object[])
				iterable = Collections.singletonList(values);
			else if (values instanceof Map<?, ?>)
				iterable = ((Map<?, ?>) values).entrySet();
			else
				iterable = Collections.singleton(values);

			if (name == null)
			{
				for (Object value : iterable)
				{
					context.add(0, value);
					template.evaluate(writer, context, parameters);
					context.remove(0);
				}
			} else
			{
				if (parameters.containsKey(name))
					throw new TemplateException(String.format("Parameter %s was already defined.", name));

				for (Object value : iterable)
				{
					parameters.put(name, value);
					template.evaluate(writer, context, parameters);
					parameters.remove(name);
				}
			}
		} catch (ExpressionException ex)
		{
			throw new TemplateException(ex.getMessage());
		}
	}

	@Override
	public String toString()
	{
		return String.format("For: %s",
			expression.toString());
	}

}
