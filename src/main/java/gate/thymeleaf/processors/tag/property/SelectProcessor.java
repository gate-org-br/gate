package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
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
public class SelectProcessor extends PropertyProcessor
{

	@Inject
	ELExpression expression;

	public SelectProcessor()
	{
		super("select");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{

		Object options = attributes.remove("options");
		if (options != null)
			options = expression.evaluate((String) options);
		else if (Boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (Enum.class.isAssignableFrom(property.getRawType()))
			options = property.getRawType().getEnumConstants();
		else
			throw new TemplateInputException("No option defined for property " + property.toString());

		String sortby = (String) attributes.remove("sortby");
		if (sortby != null)
		{
			var comparator = expression.comparator(sortby);
			options = Toolkit
				.collection(options)
				.stream()
				.sorted(comparator)
				.collect(Collectors.toList());
		}

		Object value = property.getValue(screen);

		var labels = Optional.ofNullable(attributes.remove("labels")).map(e -> (String) e).map(expression::function).orElse(Function.identity());
		var values = Optional.ofNullable(attributes.remove("values")).map(e -> (String) e).map(expression::function).orElse(Function.identity());
		var children = Optional.ofNullable(attributes.remove("children")).map(e -> (String) e).map(expression::function).orElse(null);

		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add("<select " + attributes + ">");
		string.add("<option></option>");

		Function<Object, Object> groups = extract(element, handler, "groups").map(expression::function).orElse(null);
		if (groups != null)
		{
			Toolkit.stream(options)
				.collect(Collectors.groupingBy(groups,
					LinkedHashMap::new,
					Collectors.toList()))
				.entrySet()
				.forEach(group ->
				{
					string.add("<optgroup label='" + Converter.toText(group.getKey()) + "'>");
					print(0, string, group.getValue(), labels, values, children, value);
					string.add("</optgroup>");
				});
		} else
			print(0, string, Toolkit.iterable(options), labels, values, children, property.getValue(screen));

		string.add("</select>");
		handler.replaceWith(string.toString(), false);
	}

	private void print(int level, StringJoiner string, Iterable<?> options, Function<Object, Object> labels, Function<Object, Object> values, Function<Object, Object> children, Object value)
	{
		for (Object object : options)
		{

			var option = values.apply(object);

			Attributes attributes = new Attributes();

			if (Objects.equals(option, value))
				attributes.put("selected", "selected");

			attributes.put("value", Converter.toString(option));

			string.add("<option " + attributes + ">" + "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp".repeat(level)
				+ Converter.toText(labels.apply(object)) + "</option>");

			if (children != null)
			{
				print(level + 1, string, Toolkit.iterable(children.apply(object)),
					labels, values, children, value);
			}
		}
	}
}
