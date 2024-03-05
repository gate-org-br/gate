package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.processors.tag.TagProcessor;
import gate.type.Attributes;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public abstract class PropertyProcessor extends TagProcessor
{

	@Inject
	ELExpressionFactory expressionFactory;

	public PropertyProcessor(String name)
	{
		super(name);

	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		if (element.getElementCompleteName().startsWith("g-"))
			return;

		var expression = expressionFactory.create();

		HttpServletRequest request = ((IWebContext) context).getRequest();
		Screen screen = (Screen) request.getAttribute("screen");

		Attributes attributes = Stream.of(element.getAllAttributes())
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> Objects.requireNonNullElse(e.getValue(), ""), (a, b) -> a, Attributes::new));

		var not = Stream.concat(attributes.keySet().stream()
			.filter(e -> e.startsWith("not:"))
			.map(e -> e.substring(4)),
			attributes.entrySet().stream()
				.filter(e -> e.getKey().startsWith("set:"))
				.filter(e -> !Boolean.TRUE.equals(expression.evaluate((String) e.getValue())))
				.map(e -> e.getKey().substring(4)))
			.toList();

		attributes.keySet().removeIf(e -> e.startsWith("not:"));
		attributes.keySet().removeIf(e -> e.startsWith("set:"));

		if (!attributes.containsKey("property"))
			throw new TemplateProcessingException("Missing required attribute property on g:" + getElement());

		var name = (String) attributes.remove("property");
		name = (String) expression.evaluate(name);
		var property = Property.getProperty(screen.getClass(), name);

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

		attributes.keySet().removeIf(not::contains);
		process(context, element, handler, screen, property, attributes);

	}

	protected abstract void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property, Attributes attributes);

}
