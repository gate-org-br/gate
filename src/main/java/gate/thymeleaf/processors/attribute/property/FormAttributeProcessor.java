package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class FormAttributeProcessor extends FormControlAttributeProcessor
{

	public FormAttributeProcessor()
	{
		super("g-form");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property, Object value)
	{
		handler.setAttribute("value", Converter.toString(value), AttributeValueQuotes.SINGLE);
	}
}
