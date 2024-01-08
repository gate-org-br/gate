package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class PropertyAttributeProcessor extends AbstractPropertyAttributeProcessor
{

	@Inject
	ELExpressionFactory expression;

	public PropertyAttributeProcessor()
	{
		super(null);

	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property)
	{

		if (!element.hasAttribute("title"))
		{
			String description = property.getDescription();
			if (description == null || description.isEmpty())
			{
				String displayName = property.getDisplayName();
				if (displayName != null && !displayName.isEmpty())
					handler.setAttribute("title", displayName);
			} else
				handler.setAttribute("title", description);
		}

		if (!element.hasAttribute("data-tooltip"))
		{
			String tooltip = property.getTooltip();
			if (tooltip != null && !tooltip.isEmpty())
				handler.setAttribute("data-tooltip", tooltip);
		}

		if (!property.toString().endsWith("[]"))
		{
			Object value = property.getValue(screen);
			if (value == null && element.hasAttribute("g:empty"))
				handler.setBody(Converter.toText(expression.create().evaluate(element.getAttributeValue("g:empty"))), false);
			else
				handler.setBody(property.getConverter().toText(property.getRawType(), value), false);
		} else
			handler.setBody("", false);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.LOW;
	}
}
