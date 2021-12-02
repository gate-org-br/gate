package gate.thymeleaf.processors.tag;

import gate.thymeleaf.FileEngine;
import gate.thymeleaf.TextEngine;
import java.io.IOException;
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
public class TemplateProcessor extends TagModelProcessor
{

	@Inject
	TextEngine textEngine;

	@Inject
	FileEngine fileEngine;

	public TemplateProcessor()
	{
		super("template");

	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{

		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		if (!element.hasAttribute("filename"))
			throw new TemplateProcessingException("Missing required attribute filename on g:template");

		var filename = element.getAttributeValue("filename");

		removeTag(context, model, handler);

		var content = textEngine.process(model, context);
		var request = ((IWebContext) context).getRequest();
		request.setAttribute("g-template-content", content);

		if (filename.endsWith(".jsp"))
		{
			var response = ((IWebContext) context).getResponse();
			try
			{
				request.getRequestDispatcher(filename).forward(request, response);
			} catch (IOException | ServletException ex)
			{
				throw new TemplateProcessingException(ex.getMessage(), ex);
			}
		} else
		{
			content = fileEngine.process(filename, context);
			replaceWith(context, model, handler, content);
		}

		request.removeAttribute("g-template-content");
	}
}
