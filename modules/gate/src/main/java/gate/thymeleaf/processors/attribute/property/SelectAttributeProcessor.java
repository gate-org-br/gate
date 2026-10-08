package gate.thymeleaf.processors.attribute.property;

import gate.adapter.converter.Converter;
import gate.adapter.renderer.Renderer;
import gate.annotation.Default;
import gate.lang.property.Property;
import gate.type.Attributes;
import gate.util.Toolkit;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class SelectAttributeProcessor extends FormControlAttributeProcessor
{

	public SelectAttributeProcessor()
	{
		super("select");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
	                    IElementTagStructureHandler handler, Object screen, Property property, Object value)
	{
		Object options;
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
			options = List.of();

		var comparator = extract(element, handler, "g:sortby").map(e -> (String) e)
				.map(expression::comparator)
				.orElse(null);
		if (comparator != null)
			options = Toolkit
					.collection(options)
					.stream()
					.sorted(comparator)
					.collect(Collectors.toList());

		var labels = extract(element, handler, "g:labels").map(expression::function).orElse(Function.identity());
		var values = extract(element, handler, "g:values").map(expression::function).orElse(Function.identity());
		var children = extract(element, handler, "g:children").map(e -> (String) e)
				.map(expression::function)
				.orElse(null);

		var defaultValue = Default.Extractor.extract(property.getRawType());
		var selected = value == null ? defaultValue : value;
		
		if (selected != null)
			handler.setAttribute("data-value",
					Converter.toString(selected));

		StringJoiner body = new StringJoiner("\n");

		body.add(extract(element, handler, "g:empty")
				.map(expression::evaluate)
				.map(Renderer::render)
				.map(e -> "<option disabled" + (selected == null ? " selected" : "") + ">" + e + "</option>")
				.orElse("<option></option>"));

		Function<Object, Object> groups
				= extract(element, handler, "g:groups")
				.map(expression::function).orElse(null);
		if (groups != null)
		{
			Toolkit.stream(options)
					.collect(Collectors.groupingBy(groups,
							LinkedHashMap::new,
							Collectors.toList()))
					.entrySet()
					.forEach(group ->
					{
						body.add("<optgroup label='" + Renderer.render(group.getKey()) + "'>");
						print(0, body, group.getValue(), labels, values, children, selected);
						body.add("</optgroup>");
					});
		} else
			print(0, body, Toolkit.iterable(options), labels, values, children, selected);

		handler.setBody(body.toString(), false);
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
					+ Renderer.render(labels.apply(object)) + "</option>");

			if (children != null)
			{
				print(level + 1, string, Toolkit.iterable(children.apply(object)),
						labels, values, children, value);
			}
		}
	}
}