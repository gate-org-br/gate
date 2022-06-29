package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public abstract class CheckableProcessor extends PropertyProcessor
{

	@Inject
	ELExpression expression;

	public CheckableProcessor(String name)
	{
		super(name);
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
		string.add("<g-select " + attributes + ">");

		Function<Object, Object> groups = extract(element, handler, "groups").map(expression::function).orElse(null);
		if (groups != null)
		{
			Toolkit.stream(options)
				.collect(Collectors.groupingBy(groups,
					LinkedHashMap::new,
					Collectors.toList()))
				.entrySet()
				.forEach(group -> print(string, group.getValue(), labels, values, children, property.toString(), value, 0));
		} else
			print(string, Toolkit.iterable(options), labels, values, children, property.toString(), value, 0);

		string.add("</g-select>");
		handler.replaceWith(string.toString(), false);
	}

	private void print(StringJoiner string, Iterable<?> options, Function<Object, Object> labels, Function<Object, Object> values,
		Function<Object, Object> children, String name, Object value, int depth)
	{
		for (Object option : options)
		{
			Attributes attributes = new Attributes();
			attributes.put("name", name);
			attributes.put("type", getComponentName());
			if (Toolkit.collection(value).contains(option))
				attributes.put("checked", "checked");
			attributes.put("value", Converter.toString(values.apply(option)));

			string.add(String.format("<input %s/><label style='padding-left: %dpx'>%s</label>",
				attributes.toString(),
				depth * 40,
				Converter.toText(labels.apply(option))));

			if (children != null)
				print(string, Toolkit.iterable(children.apply(option)),
					labels, values, children, name, value, depth + 1);
		}
	}

	protected abstract String getComponentName();
}
