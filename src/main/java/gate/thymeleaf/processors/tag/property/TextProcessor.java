package gate.thymeleaf.processors.tag.property;

import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TextProcessor extends PropertyProcessor
{

	public TextProcessor()
	{
		super("text");
	}

	@Override
	protected void process(Model model, Property property, Attributes attributes)
	{
		Expression expression = Expression.of(model.getContext());
		attributes.put("type", "text");
		if (model.has("value"))
			attributes.put("value", Converter.toString(expression.evaluate(model.get("value"))));
		else if (!property.toString().endsWith("[]"))
			attributes.put("value", Converter.toString(property.getValue(model.screen())));
		model.replaceAll("<input " + attributes + "/>");
	}
}
