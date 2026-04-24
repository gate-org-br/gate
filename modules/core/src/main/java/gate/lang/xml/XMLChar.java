package gate.lang.xml;

import gate.error.TemplateException;
import java.io.IOException;
import java.io.Writer;

class XMLChar implements XMLEvaluable
{

	private final char value;

	private XMLChar(char value)
	{
		this.value = value;
	}

	public static XMLChar of(char value)
	{
		return new XMLChar(value);
	}

	@Override
	public Type getType()
	{
		return Type.CHAR;
	}

	@Override
	public void evaluate(Writer writer) throws TemplateException
	{
		try
		{
			writer.write(value);
		} catch (IOException ex)
		{
			throw new TemplateException(String.format("Error trying to evaluate template: %s.", ex.getMessage()));
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
