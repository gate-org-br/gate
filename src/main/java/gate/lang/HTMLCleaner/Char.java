package gate.lang.HTMLCleaner;

import gate.error.TemplateException;
import java.io.IOException;
import java.io.Writer;

class Char implements Evaluable
{

	private final char value;

	Char(char value)
	{
		this.value = value;
	}

	@Override
	public void evaluate(Writer writer) throws TemplateException
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
