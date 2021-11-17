package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.exceptions.TemplateProcessingException;

@ApplicationScoped
public class IfProcessor extends ModelProcessor
{

	public IfProcessor()
	{
		super("if");
	}

	@Override
	protected void doProcess(Model model)
	{
		if (!model.has("condition"))
			throw new TemplateProcessingException("Missing required attribute condition on g:if");

		Expression expression = Expression.of(model.getContext());
		if ((boolean) expression.evaluate(model.get("condition")))
			model.removeTag();
		else if (model.has("otherwise"))
			model.replaceAll(Converter.toText(expression.evaluate(model.get("otherwise"))));
		else
			model.removeAll();
	}

}
