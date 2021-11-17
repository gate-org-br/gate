package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WriteProcessor extends ModelProcessor
{

	public WriteProcessor()
	{
		super("write");
	}

	@Override
	protected void doProcess(Model model)
	{
		Expression expression = Expression.of(model.getContext());
		model.replaceAll(Converter.toString(expression.evaluate(model.get("value"))));
	}
}
