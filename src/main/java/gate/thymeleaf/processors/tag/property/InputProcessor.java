package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public abstract class InputProcessor extends PropertyProcessor
{

	@Inject
	ELExpression expression;

	public InputProcessor(String name)
	{
		super(name);
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{
		attributes.put("type", getElement());
		if (attributes.containsKey("value"))
			attributes.put("value", Converter.toString(expression.evaluate((String) attributes.get("value"))));
		else if (!property.toString().endsWith("[]"))
			attributes.put("value", Converter.toString(property.getValue(screen)));
		handler.replaceWith("<input " + attributes + "/>", true);
	}
}
