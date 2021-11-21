package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class TextareaProcessor extends PropertyProcessor
{

	@Inject
	ELExpression expression;

	public TextareaProcessor()
	{
		super("textarea");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{
		if (element.hasAttribute("value"))
			handler.replaceWith("<textarea " + attributes + ">"
				+ Converter.toString(expression.evaluate(element.getAttributeValue("value"))) + "</textarea>", false);
		else if (!property.toString().endsWith("[]"))
			handler.replaceWith("<textarea " + attributes + ">" + Converter.toString(property.getValue(screen)) + "</textarea>", false);
		else
			handler.replaceWith("<textarea " + attributes + "></textarea>", false);
	}
}
