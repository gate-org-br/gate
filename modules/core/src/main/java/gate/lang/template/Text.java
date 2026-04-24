package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Parameters;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

class Text implements Evaluable
{

	private final String value;

	Text(String value)
	{
		this.value = value;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
		try
		{
			writer.write(value);
		} catch (IOException ex)
		{
			throw new TemplateException(String.format("Error trying to evaluate templete: %s.", ex.getMessage()));
		}
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return value;
	}
}
