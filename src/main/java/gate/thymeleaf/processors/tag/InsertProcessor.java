package gate.thymeleaf.processors.tag;

import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class InsertProcessor extends TagProcessor
{

	public InsertProcessor()
	{
		super("insert");

	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var request = ((IWebContext) context).getRequest();
		var content = request.getAttribute("g-template-content");
		request.removeAttribute("g-template-content");
		handler.replaceWith(content.toString(), false);
	}
}
