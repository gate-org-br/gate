package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TableProcessor extends ModelProcessor
{

	public TableProcessor()
	{
		super("table");
	}

	@Override
	protected void doProcess(Model model)
	{
		Expression expression = Expression.of(model.getContext());
		if (!model.has("condition") || (boolean) expression.evaluate(model.get("condition")))
		{
			Attributes attributes = new Attributes();
			model.stream()
				.filter(e -> e.getValue() != null)
				.filter(e -> !"condition".equals(e.getAttributeCompleteName()))
				.filter(e -> !"otherwise".equals(e.getAttributeCompleteName()))
				.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));

			model.replaceTag("table", attributes);
		} else if (model.has("otherwise"))
			model.replaceAll("<div class='TEXT'><h1>" + Converter.toText(expression.evaluate(model.get("otherwise"))) + "</h1></div>");
		else
			model.removeAll();
	}

}
