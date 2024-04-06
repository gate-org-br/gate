package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Sequence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class TextEditorAttributeProcessor extends FormControlAttributeProcessor
{

	@Inject
	Sequence sequence;

	@Inject
	ELExpressionFactory expression;

	public TextEditorAttributeProcessor()
	{
		super("g-text-editor");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property, Object value)
	{
		handler.setAttribute("value", Converter.toString(value));
	}
}
