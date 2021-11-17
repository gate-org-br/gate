package gate.thymeleaf.processors.attribute;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class InsertAttributeProcessor extends AttributeProcessor
{

	public InsertAttributeProcessor()
	{
		super("insert");

	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		handler.removeAttribute("g:insert");
		HttpServletRequest request = ((IWebContext) context).getRequest();
		handler.setBody(request.getAttribute("g-template-content").toString(), false);
	}
}
