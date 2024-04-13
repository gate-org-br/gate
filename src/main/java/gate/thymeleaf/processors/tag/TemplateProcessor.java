package gate.thymeleaf.processors.tag;

import gate.thymeleaf.FileEngine;
import gate.thymeleaf.TextEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.LinkedList;
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
	@SuppressWarnings("unchecked")
	public void process(ITemplateContext context, IModel model,
			IElementModelStructureHandler handler)
	{

		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		if (!element.hasAttribute("filename"))
			throw new TemplateProcessingException(
					"Missing required attribute filename on g:template");

		var filename = element.getAttributeValue("filename");

		removeTag(context, model, handler);

		var exchange = ((IWebContext) context).getExchange();

		if (exchange.getAttributeValue("g-template-content") == null)
			exchange.setAttributeValue("g-template-content", new LinkedList<>());
		((LinkedList<Object>) exchange.getAttributeValue("g-template-content")).add(model);
		var content = fileEngine.process(filename, context);
		replaceWith(context, model, handler, content);

		exchange.removeAttribute("g-template-content");
	}
}
