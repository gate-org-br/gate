package gate.thymeleaf.processors.tag;

import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class WhenProcessor extends TagProcessor
{

	public WhenProcessor()
	{
		super("when");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		throw new TemplateProcessingException("Attempt to use a g:otherwise outside a g:choose");
	}
}
