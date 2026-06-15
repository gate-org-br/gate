package gate.thymeleaf;

import gate.lang.property.Property;
import gate.type.Attributes;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicAttributes extends Attributes
{
	public static DynamicAttributes of(IProcessableElementTag element)
	{
		return Stream.of(element.getAllAttributes())
				.collect(Collectors.toMap(IAttribute::getAttributeCompleteName,
						e -> Objects.requireNonNullElse(e.getValue(), ""),
						(a, b) -> a,
						DynamicAttributes::new));
	}

	public void setFormAttributes(Property property)
	{
		setFormAttributes(property, this::containsKey, this::put);
	}

	public void setInfoAttributes(Property property)
	{
		setInfoAttributes(property, this::containsKey, this::put);
	}

	public static void setFormAttributes(Property property,
	                                     Predicate<String> contains,
	                                     BiConsumer<String, String> put)
	{
		put.accept("name", property.toString());

		property.getConstraints().stream()
				.filter(e -> !contains.test(e.getName()))
				.forEachOrdered(e -> put.accept(e.getName(), e.getValue().toString()));

		if (!contains.test("placeholder"))
		{
			String placeholder = property.getMetadata().placeholder();
			if (placeholder != null && !placeholder.isEmpty())
				put.accept("placeholder", placeholder);
		}
	}

	public static void setInfoAttributes(Property property,
	                                     Predicate<String> contains,
	                                     BiConsumer<String, String> put)
	{
		if (!contains.test("title"))
		{
			String description = property.getMetadata().description();
			if (description == null || description.isEmpty())
			{
				String displayName = property.getMetadata().name();
				if (displayName != null && !displayName.isEmpty())
					put.accept("title", displayName);
			} else
				put.accept("title", description);
		}

		if (!contains.test("data-tooltip"))
		{
			String tooltip = property.getMetadata().tooltip();
			if (tooltip != null && !tooltip.isEmpty())
				put.accept("data-tooltip", tooltip);
		}
	}

	public DynamicAttributes process(ELExpression expression)
	{
		entrySet().stream()
				.filter(e -> e.getKey().startsWith("set:"))
				.map(e -> Map.entry(e.getKey().substring(4),
						expression.evaluate((String) e.getValue())))
				.toList()
				.forEach(e -> put(e.getKey(), e.getValue()));
		keySet().removeIf(e -> e.startsWith("set:"));

		keySet().stream()
				.filter(e -> e.startsWith("not:"))
				.map(e -> e.substring(4))
				.toList().forEach(this::remove);
		keySet().removeIf(e -> e.startsWith("not:"));

		entrySet().stream()
				.filter(e -> e.getKey().startsWith("has:"))
				.filter(e -> !Boolean.TRUE.equals(expression.evaluate((String) e.getValue())))
				.map(e -> e.getKey().substring(4))
				.toList().forEach(this::remove);
		keySet().removeIf(e -> e.startsWith("has:"));

		return this;
	}
}