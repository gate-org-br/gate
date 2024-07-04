package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.type.Attributes;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class TextViewerProcessor extends PropertyProcessor
{

	@Inject
	ELExpressionFactory expression;

	public TextViewerProcessor()
	{
		super("text-viewer");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{

		String value = "";
		if (attributes.containsKey("value"))
			value = Converter.toString(expression.create()
				.evaluate((String) attributes.remove("value")));
		else
			value = Converter.toString(property.getValue(screen));

		attributes.put("value", value.replace('"', '\''));

		handler.replaceWith("<g-text-viewer " + attributes + "></g-text-viewer>", true);
	}
}
