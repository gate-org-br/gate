package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class TextEditorProcessor extends PropertyProcessor
{

	@Inject
	ELExpression expression;

	public TextEditorProcessor()
	{
		super("text-editor");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{
		String value = "";
		if (attributes.containsKey("value"))
			value = Converter.toString(expression.evaluate((String) attributes.remove("value")));
		else if (!property.toString().endsWith("[]"))
			value = Converter.toString(property.getValue(screen));
		handler.replaceWith("<g-text-editor " + attributes + ">" + value + "</g-text-editor>", true);
	}
}
