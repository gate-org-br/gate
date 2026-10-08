package gate.thymeleaf.processors.attribute.property;

import gate.adapter.converter.Converter;
import gate.lang.property.Property;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class TextEditorAttributeProcessor extends FormControlAttributeProcessor
{

	public TextEditorAttributeProcessor()
	{
		super("g-text-editor");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
	                    IElementTagStructureHandler handler, Object screen, Property property, Object value)
	{
		handler.setAttribute("value", Converter.toString(value)
				.replace('"', '\''));
	}
}