package gate.thymeleaf.processors.tag.iterable;

import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.TextEngine;
import gate.thymeleaf.processors.tag.TagModelProcessor;
import gate.type.Hierarchy;
import gate.util.Toolkit;
import java.util.Objects;
import java.util.function.Function;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

public abstract class IterableProcessor extends TagModelProcessor
{

	@Inject
	TextEngine engine;

	@Inject
	ELExpression expression;

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
		var source = expression.evaluate(element.getAttributeValue("source"));

		var depth = Objects.requireNonNullElse(element.getAttributeValue("depth"), "depth");
		var index = Objects.requireNonNullElse(element.getAttributeValue("index"), "index");
		var target = Objects.requireNonNullElse(element.getAttributeValue("target"), "target");

		var children = element.hasAttribute("children") ? expression.function("children") : null;

		HttpServletRequest request = ((IWebContext) context).getRequest();
		if (request.getAttribute(index) == null)
		{
			request.setAttribute(index, -1);

			if (request.getAttribute(depth) == null)
			{
				request.setAttribute(depth, -1);
				iterate(context,
					model,
					handler,
					request,
					content,
					source,
					target,
					index,
					depth,
					children);
				request.removeAttribute(depth);
			} else
				iterate(context,
					model,
					handler,
					request,
					content,
					source,
					target,
					index,
					depth,
					children);

			request.removeAttribute(index);
		} else if (request.getAttribute(depth) == null)
		{
			request.setAttribute(depth, -1);
			iterate(context,
				model,
				handler,
				request,
				content,
				source,
				target,
				index,
				depth,
				children);
			request.removeAttribute(depth);
		} else
			iterate(context,
				model,
				handler,
				request,
				content,
				source,
				target,
				index,
				depth,
				children);
	}

	private void iterate(ITemplateContext context, IModel model,
		IElementModelStructureHandler handler,
		HttpServletRequest request, IModel body, Object source,
		String target, String index, String depth, Function<Object, Object> children)
	{
		request.setAttribute(depth, ((int) request.getAttribute(depth)) + 1);
		for (Object value : Toolkit.iterable(source))
		{
			request.setAttribute(index, ((int) request.getAttribute(index)) + 1);
			if (target != null)
				request.setAttribute(target, value);
			add(context, model, handler, engine.process(body, context));
			if (target != null)
				request.removeAttribute(target);
			if (value != null && children != null)
				for (Object child : Toolkit.iterable(children.apply(value)))
					iterate(context, model, handler, request, body, child, target, index, depth, children);
			else if (value instanceof Hierarchy)
				for (Object child : Toolkit.iterable(((Hierarchy) value).getChildren()))
					iterate(context, model, handler, request, body, child, target, index, depth, children);

		}
		request.setAttribute(depth, ((int) request.getAttribute(depth)) - 1);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
