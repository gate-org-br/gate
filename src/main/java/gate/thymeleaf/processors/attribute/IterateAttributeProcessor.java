package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.TextEngine;
import gate.type.Attributes;
import gate.type.Hierarchy;
import gate.util.Toolkit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.web.IWebExchange;

@ApplicationScoped
public class IterateAttributeProcessor extends AttributeModelProcessor
{

	@Inject
	TextEngine engine;

	@Inject
	ELExpressionFactory expression;

	public IterateAttributeProcessor()
	{
		super("iterate");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IWebExchange exchange = ((IWebContext) context).getExchange();
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		var source = expression.create().evaluate(element.getAttributeValue("g:iterate"));
		var depth = Objects.requireNonNullElse(element.getAttributeValue("g:depth"), "depth");
		var index = Objects.requireNonNullElse(element.getAttributeValue("g:index"), "index");
		var target = Objects.requireNonNullElse(element.getAttributeValue("g:target"), "target");
		var children = Optional.ofNullable(element.getAttributeValue("g:children")).map(expression.create()::function).orElse(null);

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

		if (exchange.getAttributeValue(index) == null)
		{
			exchange.setAttributeValue(index, -1);
			if (exchange.getAttributeValue(depth) == null)
			{
				exchange.setAttributeValue(depth, -1);
				iterate(context, model, handler, content, exchange, source, target, index, depth, children);
				exchange.removeAttribute(depth);
			} else
				iterate(context, model, handler, content, exchange, source, target, index, depth, children);
			exchange.removeAttribute(index);
		} else if (exchange.getAttributeValue(depth) == null)
		{
			exchange.setAttributeValue(depth, -1);
			iterate(context, model, handler, content, exchange, source, target, index, depth, children);
			exchange.removeAttribute(depth);
		} else
			iterate(context, model, handler, content, exchange, source, target, index, depth, children);
	}

	private void iterate(ITemplateContext context, IModel model, IElementModelStructureHandler handler,
		IModel content, IWebExchange exchange, Object source,
		String target, String index, String depth, Function<Object, Object> children)
	{
		exchange.setAttributeValue(depth, ((int) exchange.getAttributeValue(depth)) + 1);
		for (Object value : Toolkit.iterable(source))
		{
			exchange.setAttributeValue(index, ((int) exchange.getAttributeValue(index)) + 1);

			if (target != null)
				exchange.setAttributeValue(target, value);
			add(context, model, handler, engine.process(content, context));
			if (target != null)
				exchange.removeAttribute(target);

			if (value != null)
				if (children != null)
					for (Object child : Toolkit.iterable(children.apply(value)))
						iterate(context, model, handler, content, exchange, child, target, index, depth, children);
				else if (value instanceof Hierarchy hierarchy)
					hierarchy.getChildren().forEach(child -> iterate(context, model, handler, content, exchange, child, target, index, depth, children));
		}
		exchange.setAttributeValue(depth, ((int) exchange.getAttributeValue(depth)) - 1);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
