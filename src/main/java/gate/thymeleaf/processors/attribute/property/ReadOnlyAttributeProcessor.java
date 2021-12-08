package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public abstract class ReadOnlyAttributeProcessor extends PropertyAttributeProcessor
{

	public ReadOnlyAttributeProcessor(String name)
	{
		super(name);

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
			handler.setBody(Converter.toText(property.getValue(screen)), false);
		else
			handler.setBody("", false);
	}
}
