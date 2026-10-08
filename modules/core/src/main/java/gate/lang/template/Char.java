package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Parameters;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

class Char implements Evaluable
{

	private final char value;

	Char(char value)
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

	public char getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return Character.toString(value);
	}
}
