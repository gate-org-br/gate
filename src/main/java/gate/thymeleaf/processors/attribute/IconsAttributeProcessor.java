package gate.thymeleaf.processors.attribute;

import gate.icon.Icons;
import gate.thymeleaf.Precedence;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class IconsAttributeProcessor extends AttributeProcessor
{

	public IconsAttributeProcessor()
	{
		super(null, "icons");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		String name = extract(element, handler, "g:icons").orElse("icon");
		handler.iterateElement(name, name + "$status", Icons.getInstance().get());
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
