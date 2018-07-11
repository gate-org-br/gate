package gate.lang.template;

import gate.error.ExpressionException;
import gate.error.TemplateException;
import gate.lang.expression.Expression;
import java.io.Writer;
import java.util.List;
import java.util.Map;

class TemplateIf implements Evaluable
{

	private final Expression expression;
	private final Template template;

	public TemplateIf(Expression expression, Template template)
	{
		this.expression = expression;
		this.template = template;
	}

	@Override
	public void evaluate(List<Object> context, Map<String, Object> parameters, Writer writer) throws TemplateException
	{
		try
		{
			Object check = expression.evaluate(context, parameters);
			if (check instanceof Boolean)
			{
				if (Boolean.TRUE.equals(check))
					template.evaluate(context, parameters, writer);

			} else
				throw new TemplateException("Expected Boolean and found %s", check);
		} catch (ExpressionException ex)
		{
			throw new TemplateException(ex.getMessage(), ex);
		}
	}

	@Override
	public String toString()
	{
		return String.format("If: %s",
				expression.toString());
	}

}
