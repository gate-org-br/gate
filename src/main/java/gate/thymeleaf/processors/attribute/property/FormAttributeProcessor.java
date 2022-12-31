package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class FormAttributeProcessor extends FormControlAttributeProcessor
{

	@Inject
	ELExpression expression;

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
