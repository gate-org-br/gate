package gate.thymeleaf.processors.attribute.property;

import gate.converter.Converter;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.processors.attribute.AttributeProcessor;
import gate.type.Attributes;
import gate.util.Toolkit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.LinkedHashMap;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SelectOptionsAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expression;

	public SelectOptionsAttributeProcessor()
	{
		super("select", "options");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		Object options = expression.create().evaluate(element.getAttributeValue("g:options"));
		handler.removeAttribute("g:options");

		var comparator = extract(element, handler, "g:sortby").map(e -> (String) e).map(expression.create()::comparator).orElse(null);
		if (comparator != null)
			options = Toolkit
				.collection(options)
				.stream()
				.sorted(comparator)
				.collect(Collectors.toList());

		var labels = extract(element, handler, "g:labels").map(expression.create()::function).orElse(Function.identity());
		var values = extract(element, handler, "g:values").map(expression.create()::function).orElse(Function.identity());
		var children = extract(element, handler, "g:children").map(e -> (String) e).map(expression.create()::function).orElse(null);

		StringJoiner body = new StringJoiner(System.lineSeparator());

		if (element.hasAttribute("g:empty"))
		{
			Object empty = expression.create().evaluate(element.getAttributeValue("g:empty"));
			body.add("<option value=''>" + empty + "</option>");
		} else
			body.add("<option></option>");

		Function<Object, Object> groups = extract(element, handler, "g:groups").map(expression.create()::function).orElse(null);
		if (groups != null)
		{
			Toolkit.stream(options)
				.collect(Collectors.groupingBy(groups,
					LinkedHashMap::new,
					Collectors.toList()))
				.entrySet()
				.forEach(group ->
				{
					body.add("<optgroup label='" + Converter.toText(group.getKey()) + "'>");
					print(0, body, group.getValue(), labels, values, children);
					body.add("</optgroup>");
				});
		} else
			print(0, body, Toolkit.iterable(options), labels, values, children);

		handler.setBody(body.toString(), false);
	}

	private void print(int level, StringJoiner string, Iterable<?> options, Function<Object, Object> labels, Function<Object, Object> values, Function<Object, Object> children)
	{
		for (Object object : options)
		{

			var option = values.apply(object);

			Attributes attributes = new Attributes();

			attributes.put("value", Converter.toString(option));

			string.add("<option " + attributes + ">" + "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp".repeat(level)
				+ Converter.toText(labels.apply(object)) + "</option>");

			if (children != null)
			{
				print(level + 1, string, Toolkit.iterable(children.apply(object)),
					labels, values, children);
			}
		}
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.LOW;
	}

}
