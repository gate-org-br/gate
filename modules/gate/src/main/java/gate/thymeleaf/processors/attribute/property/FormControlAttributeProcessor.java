package gate.thymeleaf.processors.attribute.property;

import gate.lang.property.Property;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public abstract class FormControlAttributeProcessor extends AbstractPropertyAttributeProcessor
{

	public FormControlAttributeProcessor(String element)
	{
		super(element);
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
						IElementTagStructureHandler handler, Object screen, Property property)
	{
		handler.setAttribute("name", property.toString());

		property.getConstraints().stream()
				.filter(e -> !element.hasAttribute(e.getName()))
				.forEachOrdered(e -> handler.setAttribute(e.getName(), e.getValue().toString()));

		if (!element.hasAttribute("title"))
		{
			String description = property.getMetadata().description();
			if (description == null || description.isEmpty())
			{
				String displayName = property.getMetadata().name();
				if (displayName != null && !displayName.isEmpty())
					handler.setAttribute("title", displayName);
			} else
				handler.setAttribute("title", description);
		}

		if (!element.hasAttribute("data-tooltip"))
		{
			String tooltip = property.getMetadata().tooltip();
			if (tooltip != null && !tooltip.isEmpty())
				handler.setAttribute("data-tooltip", tooltip);
		}

		if (!element.hasAttribute("data-confirm"))
		{
			String tooltip = property.getMetadata().tooltip();
			if (tooltip != null && !tooltip.isEmpty())
				handler.setAttribute("data-tooltip", tooltip);
		}

		if (!element.hasAttribute("placeholder"))
		{
			String placeholder = property.getMetadata().placeholder();
			if (placeholder != null && !placeholder.isEmpty())
				handler.setAttribute("placeholder", placeholder);
		}

		Object value = null;
		if (element.hasAttribute("value"))
		{
			handler.removeAttribute("value");
			value = expression.evaluate(element.getAttributeValue("value"));
		} else if (!property.toString().endsWith("[]"))
			value = property.getValue(screen);

		process(context, element, handler, screen, property, value);
	}

	public abstract void process(ITemplateContext context, IProcessableElementTag element,
								 IElementTagStructureHandler handler, Object screen, Property property, Object value);
}