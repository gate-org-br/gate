package gate.thymeleaf.processors.attribute.property;

import gate.converter.Converter;
import gate.lang.property.Property;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class TextAreaAttributeProcessor extends FormControlAttributeProcessor
{

	public TextAreaAttributeProcessor()
	{
		super("textarea");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Object screen, Property property, Object value)
	{
		handler.setBody(Converter.toString(value), false);
	}

}
