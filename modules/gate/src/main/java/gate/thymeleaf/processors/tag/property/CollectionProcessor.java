package gate.thymeleaf.processors.tag.property;

import gate.adapter.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.DynamicAttributes;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.processors.tag.TagModelProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CollectionProcessor extends TagModelProcessor
{

	@Inject
	ELExpression expression;

	public CollectionProcessor()
	{
		super("collection");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		var exchange = ((IWebContext) context).getExchange();
		Object screen = Optional.ofNullable(element.getAttributeValue("context"))
				.map(expression::evaluate)
				.orElseGet(() -> exchange.getAttributeValue("screen"));

		DynamicAttributes attributes = DynamicAttributes.of(element);

		if (!attributes.containsKey("property"))
			throw new TemplateProcessingException("Missing required attribute property on g:collection");

		var name = (String) attributes.remove("property");
		name = (String) expression.evaluate(name);
		var property = Property.getProperty(screen.getClass(), name);

		attributes.setInfoAttributes(property);
		attributes.setFormAttributes(property);
		attributes.process(expression);

		if (attributes.containsKey("value"))
			attributes.put("value", serialize(expression.evaluate((String) attributes.get("value"))));
		else if (!property.toString().endsWith("[]"))
			attributes.put("value", serialize(property.getValue(screen)));

		replaceTag(context, model, handler, "g-collection", attributes);
	}

	private String serialize(Object value)
	{
		if (!(value instanceof Collection<?> collection))
			return "";

		return collection.stream()
				.filter(Objects::nonNull)
				.map(Converter::toString)
				.collect(Collectors.joining(";"));
	}
}