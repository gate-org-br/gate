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

public abstract class ISOInputProcessor extends PropertyProcessor
{

	@Inject
	ELExpression expression;

	public ISOInputProcessor(String name)
	{
		super(name);
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{
		attributes.put("type", getElement());

		if (attributes.containsKey("value"))
		{
			var value = attributes.get("value");
			value = expression.evaluate((String) value);
			value = Converter.toISOString(value);
			attributes.put("value", value);
		} else if (!property.toString().endsWith("[]"))
		{
			var value = property.getValue(screen);
			value = Converter.toISOString(value);
			attributes.put("value", value);
		}

		handler.replaceWith("<input " + attributes + "/>", true);
	}
}
