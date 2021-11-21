package gate.thymeleaf.processors.tag.iterable;

import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Model;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.TextEngine;
import gate.thymeleaf.processors.tag.ModelProcessor;
import gate.type.Hierarchy;
import gate.util.Toolkit;
import javax.inject.Inject;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;

public abstract class IterableProcessor extends ModelProcessor
{

	@Inject
	TextEngine engine;

	@Inject
	ELExpression expression;

	public IterableProcessor(String name)
	{
		super(name);
	}

	@Override
	protected void doProcess(Model model)
	{
		process(model);
	}

	protected abstract void process(Model model);

	protected void iterate(Model model, IModel content)
	{
		if (!model.has("source"))
			throw new TemplateProcessingException("Missing required attribute source on g:" + model.getName());

		var depth = "depth";
		if (model.has("depth"))
			depth = model.get("depth");

		var index = "index";
		if (model.has("index"))
			index = model.get("index");

		var target = "target";
		if (model.has("target"))
			target = model.get("target");

		boolean createdIndex = false;
		if (model.request().getAttribute(index) == null)
		{
			createdIndex = true;
			model.request().setAttribute(index, -1);
		}

		boolean createdDepth = false;
		if (model.request().getAttribute(depth) == null)
		{
			createdDepth = true;
			model.request().setAttribute(depth, -1);
		}

		iterate(model,
			content,
			expression.evaluate(model.get("source")),
			target,
			index,
			depth,
			model.get("children"));

		if (createdDepth)
			model.request().removeAttribute(depth);

		if (createdIndex)
			model.request().removeAttribute(index);

	}

	private void iterate(Model model, IModel body, Object source,
		String target, String index, String depth, String children)
	{
		increment(model, depth);
		for (Object value : Toolkit.iterable(source))
		{
			increment(model, index);
			if (target != null)
				model.request().setAttribute(target, value);
			model.add(engine.process(body, model.getContext()));
			if (target != null)
				model.request().removeAttribute(target);
			if (value != null && children != null)
				for (Object child : Toolkit.iterable(expression.evaluate(children, value)))
					iterate(model, body, child, target, index, depth, children);
			else if (value instanceof Hierarchy)
				for (Object child : Toolkit.iterable(((Hierarchy) value).getChildren()))
					iterate(model, body, child, target, index, depth, children);

		}
		decrement(model, depth);
	}

	private void increment(Model model, String field)
	{
		Integer value = (Integer) model.request().getAttribute(field);
		value++;
		model.request().setAttribute(field, value);
	}

	private void decrement(Model model, String field)
	{
		Integer value = (Integer) model.request().getAttribute(field);
		value--;
		model.request().setAttribute(field, value);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
