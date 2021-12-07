package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.processors.tag.TagProcessor;
import gate.type.Attributes;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public abstract class PropertyProcessor extends TagProcessor
{

	public PropertyProcessor(String name)
	{
		super(name);

	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		HttpServletRequest request = ((IWebContext) context).getRequest();
		Screen screen = (Screen) request.getAttribute("screen");

		Attributes attributes = Stream.of(element.getAllAttributes())
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> e.getValue(), (a, b) -> a, Attributes::new));

		if (!attributes.containsKey("property"))
			throw new TemplateProcessingException("Missing required attribute property on g:" + getElement());
		var property = Property.getProperty(screen.getClass(), (String) attributes.remove("property"));
		attributes.put("name", property.toString());

		property.getConstraints().stream()
			.filter(e -> !attributes.containsKey(e.getName()))
			.forEachOrdered(e -> attributes.put(e.getName(), e.getValue().toString()));

		if (!attributes.containsKey("title"))
		{
			String description = property.getDescription();
			if (description == null || description.isEmpty())
			{
				String displayName = property.getDisplayName();
				if (displayName != null && !displayName.isEmpty())
					attributes.put("title", displayName);
			} else
				attributes.put("title", description);
		}

		if (!attributes.containsKey("data-tooltip"))
		{
			String tooltip = property.getTooltip();
			if (tooltip != null && !tooltip.isEmpty())
				attributes.put("data-tooltip", tooltip);
		}

		if (!attributes.containsKey("placeholder"))
		{
			String placeholder = property.getPlaceholder();
			if (placeholder != null && !placeholder.isEmpty())
				attributes.put("placeholder", placeholder);
		}

		attributes.entrySet().removeIf(e -> e.getValue() == null || e.getValue().toString().isBlank());

		process(context, element, handler, screen, property, attributes);

	}

	protected abstract void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property, Attributes attributes);

}
