package gate.lang.template;

import gate.error.ExpressionException;
import gate.error.TemplateException;
import gate.lang.expression.Expression;
import gate.lang.expression.Parameters;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class TemplateIterator implements Evaluable
{

	private final Expression source;
	private final Template template;
	private final Template elseTemplate;
	private final String target;
	private final String index;

	public TemplateIterator(Expression source, String target, String index, Template template)
	{
		this(source, target, index, template, null);
	}

	public TemplateIterator(Expression source, String target, String index,
			Template template, Template elseTemplate)
	{
		this.source = source;
		this.template = template;
		this.target = target;
		this.index = index;
		this.elseTemplate = elseTemplate;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
		try
		{
			Object values = source.evaluate(context, parameters);

			Iterable<?> iterable;
			if (values instanceof Collection<?> collection)
				iterable = collection;
			else if (values instanceof Object[])
				iterable = Collections.singletonList(values);
			else if (values instanceof Map<?, ?> map)
				iterable = map.entrySet();
			else
				iterable = Collections.singleton(values);

			int i = 0;
			boolean empty = elseTemplate != null;
			for (Object value : iterable)
			{
				empty = false;
				if (target != null)
					parameters.put(target, value);

				if (index != null)
					parameters.put(index, i++);

				template.evaluate(writer, context, parameters);

				if (target != null)
					parameters.remove(target);

				if (index != null)
					parameters.remove(index);
			}

			if (empty)
				elseTemplate.evaluate(writer, context, parameters);
		} catch (ExpressionException ex)
		{
			throw new TemplateException(ex.getMessage());
		}
	}

	@Override
	public String toString()
	{
		return String.format("For: %s", source.toString());
	}

}
