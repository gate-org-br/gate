package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class OtherwiseAttributeProcessor extends AttributeProcessor
{

	public OtherwiseAttributeProcessor()
	{
		super(null, "otherwise");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		handler.removeAttribute("g:otherwise");
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MIN;
	}
}
