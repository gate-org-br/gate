package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.FileEngine;
import gate.thymeleaf.TextEngine;
import java.io.IOException;
import java.util.LinkedList;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class TemplateAttributeModelProcessor extends AttributeModelProcessor
{

	@Inject
	TextEngine textEngine;

	@Inject
	FileEngine fileEngine;

	public TemplateAttributeModelProcessor()
	{
		super("template");

	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{

		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		var template = element.getAttributeValue("g:template");

		removeTag(context, model, handler);

		var request = ((IWebContext) context).getRequest();

		if (template.endsWith(".jsp"))
		{
			var response = ((IWebContext) context).getResponse();

			try
			{
				var content = textEngine.process(model, context);
				request.setAttribute("g-template-content", content);
				request.getRequestDispatcher(template).forward(request, response);
			} catch (IOException | ServletException ex)
			{
				throw new TemplateProcessingException(ex.getMessage(), ex);
			}
		} else
		{
			if (request.getAttribute("g-template-content") == null)
				request.setAttribute("g-template-content", new LinkedList<>());
			((LinkedList) request.getAttribute("g-template-content")).add(model);
			replaceWith(context, model, handler, fileEngine.process(template, context));
		}

		request.removeAttribute("g-template-content");
	}

}
