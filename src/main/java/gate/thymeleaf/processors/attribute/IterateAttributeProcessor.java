package gate.thymeleaf.processors.attribute;

import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Model;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.TextEngine;
import gate.type.Attributes;
import gate.type.Hierarchy;
import gate.util.Toolkit;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;

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
	protected void process(Model model)
	{
		HttpServletRequest request = ((IWebContext) model.getContext()).getRequest();

		var attributes = new Attributes();
		var source = model.get("g:iterate");
		var depth = Objects.requireNonNullElse(model.get("g:depth"), "depth");
		var index = Objects.requireNonNullElse(model.get("g:index"), "index");
		var target = Objects.requireNonNullElse(model.get("g:target"), "target");
		var children = model.get("g:children");

		model.attributes()
			.filter(e -> e.getValue() != null)
			.filter(e -> !"g:iterate".equals(e.getAttributeCompleteName()))
			.filter(e -> !"g:depth".equals(e.getAttributeCompleteName()))
			.filter(e -> !"g:index".equals(e.getAttributeCompleteName()))
			.filter(e -> !"g:target".equals(e.getAttributeCompleteName()))
			.filter(e -> !"g:children".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));

		model.replaceTag(model.tag(), attributes);
		IModel clone = model.cloneModel();
		model.removeAll();

		if (request.getAttribute(index) == null)
		{
			request.setAttribute(index, -1);
			if (request.getAttribute(depth) == null)
			{
				request.setAttribute(depth, -1);
				iterate(model, clone, request, expression.evaluate(source), target, index, depth, children);
				request.removeAttribute(depth);
			} else
				iterate(model, clone, request, expression.evaluate(source), target, index, depth, children);
			request.removeAttribute(index);
		} else if (request.getAttribute(depth) == null)
		{
			request.setAttribute(depth, -1);
			iterate(model, clone, request, expression.evaluate(source), target, index, depth, children);
			request.removeAttribute(depth);
		} else
			iterate(model, clone, request, expression.evaluate(source), target, index, depth, children);
	}

	private void iterate(Model model, IModel clone, HttpServletRequest request, Object source,
		String target, String index, String depth, String children)
	{
		increment(request, depth);
		for (Object value : Toolkit.iterable(source))
		{
			increment(request, index);

			if (target != null)
				request.setAttribute(target, value);
			model.add(engine.process(clone, model.getContext()));
			if (target != null)
				request.removeAttribute(target);

			if (value != null)
				if (children != null)
					for (Object child : Toolkit.iterable(Property.getValue(value, children)))
						iterate(model, clone, request, child, target, index, depth, children);
				else if (value instanceof Hierarchy)
					((Hierarchy) value).getChildren().forEach(child -> iterate(model, clone, request, child, target, index, depth, children));
		}
		decrement(request, depth);
	}

	private void increment(HttpServletRequest request, String field)
	{
		Integer value = (Integer) request.getAttribute(field);
		value++;
		request.setAttribute(field, value);
	}

	private void decrement(HttpServletRequest request, String field)
	{
		Integer value = (Integer) request.getAttribute(field);
		value--;
		request.setAttribute(field, value);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
