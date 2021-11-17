package gate.thymeleaf.processors.tag.property;

import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TextareaProcessor extends PropertyProcessor
{

	public TextareaProcessor()
	{
		super("textarea");
	}

	@Override
	protected void process(Model model, Property property, Attributes attributes)
	{
		Expression expression = Expression.of(model.getContext());
		if (model.has("value"))
			model.replaceAll("<textarea " + attributes + ">" + Converter.toString(expression.evaluate(model.get("value"))) + "</textarea>");
		else if (!property.toString().endsWith("[]"))
			model.replaceAll("<textarea " + attributes + ">" + Converter.toString(property.getValue(model.screen())) + "</textarea>");
		else
			model.replaceAll("<textarea " + attributes + "></textarea>");
	}

}
