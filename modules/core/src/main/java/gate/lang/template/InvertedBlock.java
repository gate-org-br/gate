package gate.lang.template;

import gate.error.ExpressionException;
import gate.error.TemplateException;
import gate.lang.expression.Expression;
import gate.lang.expression.Parameters;
import gate.util.Toolkit;
import java.io.Writer;
import java.util.List;

class InvertedBlock implements Evaluable
{

	private final Expression expression;
	private final Template template;

	public InvertedBlock(Expression expression, Template template)
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
			if (Toolkit.isFalsy(object))
				template.evaluate(writer, context, parameters);
		} catch (ExpressionException ex)
		{
			throw new TemplateException(ex.getMessage());
		}
	}

	@Override
	public String toString()
	{
		return String.format("Inverted block: %s",
				expression.toString());
	}

}
