package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.FileEngine;
import gate.thymeleaf.TextEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.LinkedList;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.web.IWebExchange;

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

		IWebExchange exchange = ((IWebContext) context).getExchange();

		if (exchange.getAttributeValue("g-template-content") == null)
			exchange.setAttributeValue("g-template-content", new LinkedList<>());
		((LinkedList) exchange.getAttributeValue("g-template-content")).add(model);
		replaceWith(context, model, handler, fileEngine.process(template, context));

		exchange.removeAttribute("g-template-content");
	}

}
