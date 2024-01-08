package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
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
	ELExpressionFactory expression;

	public TextareaProcessor()
	{
		super("textarea");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{
		String value = "";
		if (attributes.containsKey("value"))
			value = property.getConverter().toString(property.getRawType(), expression.create().evaluate((String) attributes.remove("value")));
		else if (!property.toString().endsWith("[]"))
			value = property.getConverter().toString(property.getRawType(), property.getValue(screen));
		handler.replaceWith("<textarea " + attributes + ">" + value + "</textarea>", true);
	}
}
