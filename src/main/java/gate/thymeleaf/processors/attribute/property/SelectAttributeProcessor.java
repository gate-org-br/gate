package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SelectAttributeProcessor extends FormControlAttributeProcessor
{

	@Inject
	ELExpression expression;

	public SelectAttributeProcessor()
	{
		super("select");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property, Object value)
	{
		Object options = null;
		if (element.hasAttribute("g:options"))
		{
			options = expression.evaluate(element.getAttributeValue("g:options"));
			handler.removeAttribute("g:options");
		} else if (Boolean.class.isAssignableFrom(property.getRawType()))
			options = List.of(Boolean.FALSE, Boolean.TRUE);
		else if (boolean.class.isAssignableFrom(property.getRawType()))
			options = List.of(Boolean.FALSE, Boolean.TRUE);
		else if (Enum.class.isAssignableFrom(property.getRawType()))
			options = property.getRawType().getEnumConstants();
		else
			throw new TemplateInputException("No option defined for property " + property.toString());

		var comparator = extract(element, handler, "g:sortby").map(e -> (String) e).map(expression::comparator).orElse(null);
		if (comparator != null)
			options = Toolkit
				.collection(options)
				.stream()
				.sorted(comparator)
				.collect(Collectors.toList());

		var labels = extract(element, handler, "g:labels").map(expression::function).orElse(Function.identity());
		var values = extract(element, handler, "g:values").map(expression::function).orElse(Function.identity());

		StringJoiner body = new StringJoiner(System.lineSeparator());
		body.add("<option></option>");

		Function<Object, Object> groups = extract(element, handler, "g:groups").map(expression::function).orElse(null);
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
					print(body, group.getValue(), labels, values, value);
					body.add("</optgroup>");
				});
		} else
			print(body, Toolkit.iterable(options), labels, values, value);

		handler.setBody(body.toString(), false);
	}

	private String print(StringJoiner result,
		Iterable<?> options,
		Function<Object, Object> labels, Function<Object, Object> values,
		Object value)
	{
		for (Object option : options)
		{
			var label = Converter.toText(labels.apply(option));

			option = values.apply(option);

			Attributes attributes = new Attributes();

			attributes.put("value", Converter.toString(option));
			if (Objects.equals(option, value))
				attributes.put("selected", "selected");

			result.add("<option " + attributes + ">" + label + "</option>");
		}
		return result.toString();
	}

}
