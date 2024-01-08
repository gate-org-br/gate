package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.type.Attributes;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public abstract class InputProcessor extends PropertyProcessor
{

	@Inject
	ELExpressionFactory expression;

	public InputProcessor(String name)
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
			value = expression.create().evaluate((String) value);
			value = property.getConverter().toString(property.getRawType(), value);
			attributes.put("value", (String) value);
		} else if (!property.toString().endsWith("[]"))
		{
			var value = property.getValue(screen);
			value = property.getConverter().toString(property.getRawType(), value);
			attributes.put("value", value);
		}

		handler.replaceWith("<input " + attributes + "/>", true);
	}
}
