package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.lang.property.Property;
import java.util.stream.Stream;
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
		IElementTagStructureHandler handler, Screen screen, Property property)
	{
		handler.setAttribute("name", property.toString());

		var expression = expressionFactory.create();

		var not = Stream.concat(Stream.of(element.getAllAttributes())
			.filter(e -> e.getAttributeCompleteName().startsWith("not:"))
			.peek(e -> handler.removeAttribute(e.getAttributeCompleteName()))
			.map(e -> e.getAttributeCompleteName().substring(4)),
			Stream.of(element.getAllAttributes())
				.filter(e -> e.getAttributeCompleteName().startsWith("set:"))
				.peek(e -> handler.removeAttribute(e.getAttributeCompleteName()))
				.filter(e -> !Boolean.TRUE.equals(expression.evaluate(e.getValue())))
				.map(e -> e.getAttributeCompleteName().substring(4)))
			.toList();

		property.getConstraints().stream()
			.filter(e -> !element.hasAttribute(e.getName()))
			.filter(e -> !not.contains(e.getName()))
			.forEachOrdered(e -> handler.setAttribute(e.getName(), e.getValue().toString()));

		if (!element.hasAttribute("title") && !not.contains("title"))
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

		if (!element.hasAttribute("data-tooltip") && !not.contains("data-tooltip"))
		{
			String tooltip = property.getTooltip();
			if (tooltip != null && !tooltip.isEmpty())
				handler.setAttribute("data-tooltip", tooltip);
		}

		if (!element.hasAttribute("data-confirm") && !not.contains("data-confirm"))
		{
			String tooltip = property.getTooltip();
			if (tooltip != null && !tooltip.isEmpty())
				handler.setAttribute("data-tooltip", tooltip);
		}

		if (!element.hasAttribute("placeholder") && !not.contains("placeholder"))
		{
			String placeholder = property.getPlaceholder();
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

		not.forEach(handler::removeAttribute);

		process(context, element, handler, screen, property, value);
	}

	public abstract void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property, Object value);
}
