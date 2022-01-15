package gate.thymeleaf.processors.attribute;

import gate.type.Attributes;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class InsertAttributeProcessor extends AttributeModelProcessor
{

	public InsertAttributeProcessor()
	{
		super(null, "insert");

	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);
		Attributes attributes
			= Stream.of(element.getAllAttributes())
				.filter(e -> e.getValue() != null)
				.filter(e -> !"g:insert".equals(e.getAttributeCompleteName()))
				.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
					e -> e.getValue(), (a, b) -> a, Attributes::new));

		HttpServletRequest request = ((IWebContext) context).getRequest();
		IModel content = (IModel) ((LinkedList) request.getAttribute("g-template-content")).removeLast();
		replaceTag(context, model, handler, element.getElementCompleteName(), attributes);
		model.insertModel(1, content);

	}
//
//	@Override
//	public void process(ITemplateContext context,
//		IProcessableElementTag element,
//		IElementTagStructureHandler handler)
//	{
//		handler.removeAttribute("g:insert");
//		HttpServletRequest request = ((IWebContext) context).getRequest();
//		Object content = request.getAttribute("g-template-content");
//		request.removeAttribute("g-template-content");
//		handler.setBody(content.toString(), false);
//	}
}
