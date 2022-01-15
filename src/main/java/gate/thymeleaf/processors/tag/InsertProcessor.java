package gate.thymeleaf.processors.tag;

import java.util.LinkedList;
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
		IModel content = (IModel) ((LinkedList) request.getAttribute("g-template-content")).removeLast();
		model.reset();
		model.insertModel(0, content);

	}
//
//	@Override
//	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
//	{
//		var request = ((IWebContext) context).getRequest();
//		var content = request.getAttribute("g-template-content");
//		request.removeAttribute("g-template-content");
//		handler.replaceWith(content.toString(), false);
//	}
}
