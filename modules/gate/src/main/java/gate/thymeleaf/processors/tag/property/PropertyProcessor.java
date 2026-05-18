package gate.thymeleaf.processors.tag.property;

import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.processors.tag.TagProcessor;
import gate.type.Attributes;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PropertyProcessor extends TagProcessor
{

	@Inject
	ELExpression expression;

	public PropertyProcessor(String name)
	{
		super(name);

	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		if (element.getElementCompleteName().startsWith("g-"))
			return;

		var exchange = ((IWebContext) context).getExchange();

		Object screen = Optional.ofNullable(element.getAttributeValue("context"))
				.map(expression::evaluate)
				.orElseGet(() -> exchange.getAttributeValue("screen"));

		Attributes attributes = Stream.of(element.getAllAttributes())
				.collect(Collectors.toMap(IAttribute::getAttributeCompleteName,
						e -> Objects.requireNonNullElse(e.getValue(), ""), (a, b) -> a, Attributes::new));

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
			String description = property.getMetadata().description();
			if (description == null || description.isEmpty())
			{
				String displayName = property.getMetadata().name();
				if (displayName != null && !displayName.isEmpty())
					attributes.put("title", displayName);
			} else
				attributes.put("title", description);
		}

		if (!attributes.containsKey("data-tooltip"))
		{
			String tooltip = property.getMetadata().tooltip();
			if (tooltip != null && !tooltip.isEmpty())
				attributes.put("data-tooltip", tooltip);
		}

		if (!attributes.containsKey("placeholder"))
		{
			String placeholder = property.getMetadata().placeholder();
			if (placeholder != null && !placeholder.isEmpty())
				attributes.put("placeholder", placeholder);
		}

		attributes.entrySet().stream()
				.filter(e -> e.getKey().startsWith("set:"))
				.map(e -> Map.entry(e.getKey().substring(4),
						expression.evaluate((String) e.getValue())))
				.toList()
				.forEach(e -> attributes.put(e.getKey(), e.getValue()));
		attributes.keySet().removeIf(e -> e.startsWith("set:"));

		attributes.keySet().stream()
				.filter(e -> e.startsWith("not:"))
				.map(e -> e.substring(4))
				.toList().forEach(attributes::remove);
		attributes.keySet().removeIf(e -> e.startsWith("not:"));

		attributes.entrySet().stream()
				.filter(e -> e.getKey().startsWith("has:"))
				.filter(e -> !Boolean.TRUE.equals(expression.evaluate((String) e.getValue())))
				.map(e -> e.getKey().substring(4))
				.toList().forEach(attributes::remove);
		attributes.keySet().removeIf(e -> e.startsWith("has:"));

		process(context, element, handler, screen, property, attributes);

	}

	protected abstract void process(ITemplateContext context, IProcessableElementTag element,
	                                IElementTagStructureHandler handler, Object screen, Property property, Attributes attributes);

}
