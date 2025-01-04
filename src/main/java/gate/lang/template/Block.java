package gate.lang.template;

import gate.error.ExpressionException;
import gate.error.TemplateException;
import gate.lang.expression.Expression;
import gate.lang.expression.Parameters;
import gate.util.Toolkit;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class Block implements Evaluable
{

	private final Expression expression;
	private final Template template;

	public Block(Expression expression, Template template)
	{
		this.expression = expression;
		this.template = template;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
		try
		{
			Object object = expression.evaluate(context, parameters);

			if (!Toolkit.isFalsy(object))
			{
				if (object instanceof Object[])
					iterate(writer, context, parameters, Arrays.asList(object));
				else if (object instanceof Iterable<?> iterable)
					iterate(writer, context, parameters, iterable);
				else
					process(writer, context, parameters, object);
			}

		} catch (ExpressionException ex)
		{
			throw new TemplateException(ex.getMessage());
		}
	}

	private void iterate(Writer writer, List<Object> context, Parameters parameters, Iterable<?> iterable)
	{
		var index = 0;
		var iterator = iterable.iterator();
		while (iterator.hasNext())
		{
			context.add(0, iterator.next());
			parameters.push(new HashMap<>());
			parameters.put("index", index);
			parameters.put("hasNext", iterator.hasNext());
			template.evaluate(writer, context, parameters);
			context.remove(0);
			parameters.poll();
			index++;
		}
	}

	private void process(Writer writer, List<Object> context, Parameters parameters, Object object)
	{
		template.evaluate(writer, context, parameters);
	}

	@Override
	public String toString()
	{
		return String.format("Block: %s: %s",
				expression, template);
	}
}
