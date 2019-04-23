package gate.lang.template;

import gate.error.TemplateException;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

class TemplateToken implements Evaluable
{

	private final Type type;
	private final String value;

	public TemplateToken(Type type, String value)
	{
		this.type = type;
		this.value = value;
	}

	public Type getType()
	{
		return type;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public void evaluate(List<Object> context,
		Map<String, Object> parameters, Writer writer) throws TemplateException
	{
		try
		{
			writer.write(value);
		} catch (IOException ex)
		{
			throw new TemplateException(String.format("Error trying to evaluate templete: %s.", ex.getMessage()));
		}
	}

	@Override
	public String toString()
	{
		return String.format("%s: %s", type, value);
	}

	public enum Type
	{
		TEXT, EXPRESSION_HEAD, EXPRESSION_TAIL,
		IF_HEAD, IF_TAIL, FOR_HEAD, FOR_TAIL, NAME,
		CONDITION, EACH, QUOTE, DOUBLE_QUOTE, CLOSE_TAG, EQUALS, EOF
	};
}
