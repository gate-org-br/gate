package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.HTMLFileEngine;
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
	HTMLFileEngine fileEngine;

	@Inject
	ELExpressionFactory expression;

	public TemplateAttributeModelProcessor()
	{
		super("template");

	}

	@Override
	public void process(ITemplateContext context, IModel model,
		IElementModelStructureHandler handler)
	{

		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		var template = (String) expression.create()
			.evaluate(element.getAttributeValue("g:template"));

		IWebExchange exchange = ((IWebContext) context).getExchange();

		removeTag(context, model, handler);

		if ("1".equals(exchange.getRequest().getHeaderValue("X-G-Fragment")))
			return;

		if (exchange.getAttributeValue("g-template-content") == null)
			exchange.setAttributeValue("g-template-content", new LinkedList<IModel>());
		((LinkedList<Object>) exchange.getAttributeValue("g-template-content")).add(model);
		replaceWith(context, model, handler, fileEngine.process(template, context));
		exchange.removeAttribute("g-template-content");
	}

}
