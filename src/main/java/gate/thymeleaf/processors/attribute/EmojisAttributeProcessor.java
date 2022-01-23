package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.Precedence;
import gate.util.Emojis;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class EmojisAttributeProcessor extends AttributeProcessor
{

	public EmojisAttributeProcessor()
	{
		super(null, "emojis");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		String name = extract(element, handler, "g:emojis").orElse("emoji");
		handler.iterateElement(name, name + "$status", Emojis.getInstance().get());
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
