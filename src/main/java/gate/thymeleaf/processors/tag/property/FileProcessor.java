package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class FileProcessor extends PropertyProcessor
{

	@Inject
	ELExpressionFactory expression;

	public FileProcessor()
	{
		super("file");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{
		attributes.put("type", "file");
		handler.replaceWith("<input " + attributes + "/>", true);
	}
}
