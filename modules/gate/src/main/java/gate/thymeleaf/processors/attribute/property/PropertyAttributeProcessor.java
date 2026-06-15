package gate.thymeleaf.processors.attribute.property;

import gate.adapter.converter.Converter;
import gate.adapter.renderer.Renderer;

import gate.lang.property.Property;
import gate.thymeleaf.DynamicAttributes;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class PropertyAttributeProcessor extends AbstractPropertyAttributeProcessor
{

	public PropertyAttributeProcessor() {super(null);}

	@Override
	public void process(ITemplateContext context,
	                    IProcessableElementTag element,
	                    IElementTagStructureHandler handler, Object screen, Property property)
	{
		DynamicAttributes.setInfoAttributes(property, element::hasAttribute, handler::setAttribute);

		var webComponent = element.getElementCompleteName().contains("-");
		if (webComponent)
			DynamicAttributes.setFormAttributes(property,
					element::hasAttribute, handler::setAttribute);

		if (!property.toString().endsWith("[]"))
		{
			Object value = property.getValue(screen);
			var empty = extract(element, handler, "g:empty")
					.map(expression::evaluate)
					.map(Renderer::render)
					.orElse(null);
			if (value == null)
				value = empty;

			if (webComponent)
				handler.setAttribute("value", Converter.toString(value));
			else
				handler.setBody(property.getRenderer()
						.render(property.getRawType(), value), false);
		} else if (webComponent)
			handler.setAttribute("value", "");
		else
			handler.setBody("", false);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.LOW;
	}
}