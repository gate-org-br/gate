package gate.lang.template;

import gate.error.ExpressionException;
import gate.error.TemplateException;
import gate.lang.expression.Expression;
import gate.lang.expression.Parameters;
import java.io.Writer;
import java.util.List;

class TemplateIf implements Evaluable
{

	private final Expression expression;
	private final Template template;
	private final Template elseTemplate;

	public TemplateIf(Expression expression, Template template)
	{
		this(expression, template, null);
	}

	public TemplateIf(Expression expression, Template template, Template elseTemplate)
	{
		this.expression = expression;
		this.template = template;
		this.elseTemplate = elseTemplate;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
		try
		{
			Object check = expression.evaluate(context, parameters);
			if (check instanceof Boolean)
			{
				if (Boolean.TRUE.equals(check))
					template.evaluate(writer, context, parameters);
				else if (elseTemplate != null)
					elseTemplate.evaluate(writer, context, parameters);

			} else
				throw new TemplateException(String.format("Expected Boolean and found %s", check));
		} catch (ExpressionException ex)
		{
			throw new TemplateException(ex.getMessage());
		}
	}

	@Override
	public String toString()
	{
		return String.format("If: %s",
				expression.toString());
	}

}
