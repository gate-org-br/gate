package gate.thymeleaf.processors.attribute.property;

import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.processors.attribute.AttributeProcessor;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public abstract class AbstractPropertyAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public AbstractPropertyAttributeProcessor(String element)
	{
		super(element, "property");
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		var exchange = ((IWebContext) context).getExchange();

		Object screen = extract(element, handler, "g:context")
			.map(expression::evaluate)
			.orElseGet(() -> exchange.getAttributeValue("screen"));

		Property property = extract(element, handler, "g:property")
			.map(expression::evaluate)
			.filter(e -> e instanceof String)
			.map(e -> (String) e)
			.map(e -> Property.getProperty(screen.getClass(), e))
			.orElseThrow(() -> new TemplateProcessingException("Missing or invalid g:property"));

		process(context, element, handler, screen, property);
	}

	public abstract void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Object screen, Property property);
}
