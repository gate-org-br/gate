package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Parameters;
import java.io.Writer;
import java.util.List;

class None implements Evaluable
{

	private None()
	{

	}

	static None INSTANCE = new None();

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
	}

	@Override
	public String toString()
	{
		return "Nothing";
	}
}
