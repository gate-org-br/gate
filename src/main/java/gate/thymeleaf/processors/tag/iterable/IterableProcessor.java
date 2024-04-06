package gate.thymeleaf.processors.tag.iterable;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.TextEngine;
import gate.thymeleaf.processors.tag.TagModelProcessor;
import gate.type.Hierarchy;
import gate.util.Toolkit;
import jakarta.inject.Inject;
import java.util.Objects;
import java.util.function.Function;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.web.IWebExchange;

public abstract class IterableProcessor extends TagModelProcessor
{

	@Inject
	TextEngine engine;

	@Inject
	ELExpressionFactory expression;

	public IterableProcessor(String name)
	{
		super(name);
	}

	protected void iterate(ITemplateContext context, IModel model,
		IElementModelStructureHandler handler, IProcessableElementTag element,
		IModel content)
	{
		if (!element.hasAttribute("source"))
			throw new TemplateProcessingException("Missing required attribute source on g:" + getName());
		var source = expression.create().evaluate(element.getAttributeValue("source"));

		var depth = Objects.requireNonNullElse(element.getAttributeValue("depth"), "depth");
		var index = Objects.requireNonNullElse(element.getAttributeValue("index"), "index");
		var target = Objects.requireNonNullElse(element.getAttributeValue("target"), "target");

		var children = element.hasAttribute("children") ? expression.create().function("children") : null;

		var exchange = ((IWebContext) context).getExchange();
		if (exchange.getAttributeValue(index) == null)
		{
			exchange.setAttributeValue(index, -1);

			if (exchange.getAttributeValue(depth) == null)
			{
				exchange.setAttributeValue(depth, -1);
				iterate(context,
					model,
					handler,
					exchange,
					content,
					source,
					target,
					index,
					depth,
					children);
				exchange.removeAttribute(depth);
			} else
				iterate(context,
					model,
					handler,
					exchange,
					content,
					source,
					target,
					index,
					depth,
					children);

			exchange.removeAttribute(index);
		} else if (exchange.getAttributeValue(depth) == null)
		{
			exchange.setAttributeValue(depth, -1);
			iterate(context,
				model,
				handler,
				exchange,
				content,
				source,
				target,
				index,
				depth,
				children);
			exchange.removeAttribute(depth);
		} else
			iterate(context,
				model,
				handler,
				exchange,
				content,
				source,
				target,
				index,
				depth,
				children);
	}

	private void iterate(ITemplateContext context, IModel model,
		IElementModelStructureHandler handler,
		IWebExchange exchange, IModel body, Object source,
		String target, String index, String depth, Function<Object, Object> children)
	{
		exchange.setAttributeValue(depth, ((int) exchange.getAttributeValue(depth)) + 1);
		for (Object value : Toolkit.iterable(source))
		{
			exchange.setAttributeValue(index, ((int) exchange.getAttributeValue(index)) + 1);
			if (target != null)
				exchange.setAttributeValue(target, value);
			add(context, model, handler, engine.process(body, context));
			if (target != null)
				exchange.removeAttribute(target);
			if (value != null && children != null)
				for (Object child : Toolkit.iterable(children.apply(value)))
					iterate(context, model, handler, exchange, body, child, target, index, depth, children);
			else if (value instanceof Hierarchy)
				for (Object child : Toolkit.iterable(((Hierarchy) value).getChildren()))
					iterate(context, model, handler, exchange, body, child, target, index, depth, children);

		}
		exchange.setAttributeValue(depth, ((int) exchange.getAttributeValue(depth)) - 1);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
