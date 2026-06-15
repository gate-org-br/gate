package gate.thymeleaf.processors.attribute.property;

import gate.lang.property.Property;
import gate.thymeleaf.DynamicAttributes;
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
		DynamicAttributes.setInfoAttributes(property, element::hasAttribute, handler::setAttribute);
		DynamicAttributes.setFormAttributes(property, element::hasAttribute, handler::setAttribute);

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