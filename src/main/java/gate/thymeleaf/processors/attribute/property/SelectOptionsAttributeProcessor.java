package gate.thymeleaf.processors.attribute.property;

import gate.converter.Converter;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.processors.attribute.AttributeProcessor;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.LinkedHashMap;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
					print(body, group.getValue(), labels, values);
					body.add("</optgroup>");
				});
		} else
			print(body, Toolkit.iterable(options), labels, values);

		handler.setBody(body.toString(), false);
	}

	private String print(StringJoiner result,
		Iterable<?> options,
		Function<Object, Object> labels, Function<Object, Object> values)
	{
		for (Object option : options)
		{
			var label = Converter.toText(labels.apply(option));

			option = values.apply(option);

			Attributes attributes = new Attributes();

			attributes.put("value", Converter.toString(option));

			result.add("<option " + attributes + ">" + label + "</option>");
		}
		return result.toString();
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.LOW;
	}

}
