package gate.thymeleaf.processors.tag;

import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class InsertProcessor extends TagModelProcessor
{

	public InsertProcessor()
	{
		super("insert");

	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		var request = ((IWebContext) context).getRequest();
		var content = request.getAttribute("g-template-content");
		request.removeAttribute("g-template-content");
		replaceWith(context, model, handler, content.toString());
	}
}
