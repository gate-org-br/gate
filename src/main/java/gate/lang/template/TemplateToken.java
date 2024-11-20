package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Parameters;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

enum TemplateToken implements Evaluable
{
	EOF(""),
	PARENT("../"),
	BLOCK("{{#"),
	BLOCK_END("{{/"),
	INVERTED_BLOCK("{{^"),
	BLOCK_TAIL("}}"),
	EXPRESSION_HEAD("{{"),
	EXPRESSION_TAIL("}}"),
	EL_HEAD("${"),
	EL_TAIL("}");

	private final String string;

	TemplateToken(String string)
	{
		this.string = string;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
		try
		{
			writer.write(string);
		} catch (IOException ex)
		{
			throw new TemplateException(String.format("Error trying to evaluate templete: %s.", ex.getMessage()));
		}
	}

	@Override
	public String toString()
	{
		return string;
	}
}
