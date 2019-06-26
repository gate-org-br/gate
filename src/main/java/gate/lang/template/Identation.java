package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Parameters;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

class Identation implements Evaluable
{

	private final String text;

	public Identation(String text)
	{
		this.text = text;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
		try
		{
			writer.write(text);
		} catch (IOException ex)
		{
			throw new TemplateException(String.format("Error trying to evaluate template: %s.", ex.getMessage()));
		}
	}

	@Override
	public String toString()
	{
		return "Identation";
	}

}
