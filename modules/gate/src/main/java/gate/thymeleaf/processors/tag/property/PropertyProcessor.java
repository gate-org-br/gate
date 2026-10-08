package gate.thymeleaf.processors.tag.property;

import gate.lang.property.Property;
import gate.thymeleaf.DynamicAttributes;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.processors.tag.TagProcessor;
import gate.type.Attributes;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.Optional;

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

		DynamicAttributes attributes = DynamicAttributes.of(element);

		if (!attributes.containsKey("property"))
			throw new TemplateProcessingException("Missing required attribute property on g:" + getElement());

		var name = (String) attributes.remove("property");
		name = (String) expression.evaluate(name);
		var property = Property.getProperty(screen.getClass(), name);

		attributes.setInfoAttributes(property);
		attributes.setFormAttributes(property);
		attributes.process(expression);

		process(context, element, handler, screen, property, attributes);

	}

	protected abstract void process(ITemplateContext context, IProcessableElementTag element,
	                                IElementTagStructureHandler handler, Object screen, Property property, Attributes attributes);

}