package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.TextEngine;
import gate.type.Attributes;
import gate.type.Hierarchy;
import gate.util.Toolkit;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class IterateAttributeProcessor extends AttributeModelProcessor
{

	@Inject
	TextEngine engine;

	@Inject
	ELExpression expression;

	public IterateAttributeProcessor()
	{
		super("iterate");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		HttpServletRequest request = ((IWebContext) context).getRequest();
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		var source = expression.evaluate(element.getAttributeValue("g:iterate"));
		var depth = Objects.requireNonNullElse(element.getAttributeValue("g:depth"), "depth");
		var index = Objects.requireNonNullElse(element.getAttributeValue("g:index"), "index");
		var target = Objects.requireNonNullElse(element.getAttributeValue("g:target"), "target");
		var children = Optional.ofNullable(element.getAttributeValue("g:children")).map(expression::function).orElse(null);

		Attributes attributes
			= Stream.of(element.getAllAttributes())
				.filter(e -> e.getValue() != null)
				.filter(e -> !"g:iterate".equals(e.getAttributeCompleteName()))
				.filter(e -> !"g:depth".equals(e.getAttributeCompleteName()))
				.filter(e -> !"g:index".equals(e.getAttributeCompleteName()))
				.filter(e -> !"g:target".equals(e.getAttributeCompleteName()))
				.filter(e -> !"g:children".equals(e.getAttributeCompleteName()))
				.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
					e -> e.getValue(), (a, b) -> a, Attributes::new));
		replaceTag(context, model, handler, element.getElementCompleteName(), attributes);

		IModel content = model.cloneModel();
		model.reset();

		if (request.getAttribute(index) == null)
		{
			request.setAttribute(index, -1);
			if (request.getAttribute(depth) == null)
			{
				request.setAttribute(depth, -1);
				iterate(context, model, handler, content, request, source, target, index, depth, children);
				request.removeAttribute(depth);
			} else
				iterate(context, model, handler, content, request, source, target, index, depth, children);
			request.removeAttribute(index);
		} else if (request.getAttribute(depth) == null)
		{
			request.setAttribute(depth, -1);
			iterate(context, model, handler, content, request, source, target, index, depth, children);
			request.removeAttribute(depth);
		} else
			iterate(context, model, handler, content, request, source, target, index, depth, children);
	}

	private void iterate(ITemplateContext context, IModel model, IElementModelStructureHandler handler,
		IModel content, HttpServletRequest request, Object source,
		String target, String index, String depth, Function<Object, Object> children)
	{
		request.setAttribute(depth, ((int) request.getAttribute(depth)) + 1);
		for (Object value : Toolkit.iterable(source))
		{
			request.setAttribute(index, ((int) request.getAttribute(index)) + 1);

			if (target != null)
				request.setAttribute(target, value);
			add(context, model, handler, engine.process(content, context));
			if (target != null)
				request.removeAttribute(target);

			if (value != null)
				if (children != null)
					for (Object child : Toolkit.iterable(children.apply(value)))
						iterate(context, model, handler, content, request, child, target, index, depth, children);
				else if (value instanceof Hierarchy)
					((Hierarchy) value).getChildren().forEach(child -> iterate(context, model, handler, content, request, child, target, index, depth, children));
		}
		request.setAttribute(depth, ((int) request.getAttribute(depth)) - 1);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
