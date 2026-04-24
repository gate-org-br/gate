package gate.lang.xml;

import gate.error.EvaluableException;
import gate.error.TemplateException;
import java.io.IOException;
import java.io.Writer;

enum XMLToken implements XMLEvaluable
{
	EOF(""),
	OPEN_TAG("<"),
	CLOSE_TAG(">"),
	QUOTE("'"),
	DOUBLE_QUOTE("\"");

	private final String string;

	XMLToken(String string)
	{
		this.string = string;
	}

	@Override
	public XMLEvaluable.Type getType()
	{
		return XMLEvaluable.Type.TOKEN;
	}

	@Override
	public void evaluate(Writer writer) throws EvaluableException
	{
		try
		{
			writer.write(string);
		} catch (IOException ex)
		{
			throw new TemplateException(String.format("Error trying to evaluate template: %s.", ex.getMessage()));
		}
	}

	@Override
	public String toString()
	{
		return string;
	}
}
