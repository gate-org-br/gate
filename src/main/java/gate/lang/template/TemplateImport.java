package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Parameters;
import java.io.Writer;
import java.util.List;

class TemplateImport implements Evaluable
{

	private final Template template;

	public TemplateImport(Template template)
	{
		this.template = template;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
		template.evaluate(writer, context, parameters);
	}

	@Override
	public String toString()
	{
		return String.format("Import: %s", template.toString());
	}

}
