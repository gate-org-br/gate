package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.exceptions.TemplateProcessingException;

@ApplicationScoped
public class PrintProcessor extends ModelProcessor
{

	public PrintProcessor()
	{
		super("print");
	}

	@Override
	protected void doProcess(Model model)
	{
		if (!model.has("value"))
			throw new TemplateProcessingException("Missing required attribute value on g:print");

		Expression expression = Expression.of(model.getContext());

		var value = expression.evaluate(model.get("value"));
		String string = Converter.toText(value, model.get("format"));
		if (string.isBlank() && model.has("empty"))
			string = model.get("empty");
		string = string.replaceAll("\\n", "<br/>");

		model.replaceAll(string);
	}
}
