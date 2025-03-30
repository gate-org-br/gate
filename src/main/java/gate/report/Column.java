package gate.report;

import gate.lang.json.JsonBoolean;
import gate.lang.json.JsonCollection;
import gate.lang.json.JsonNumber;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonScalar;
import gate.lang.json.JsonString;
import gate.type.Color;
import java.util.ArrayList;
import java.util.List;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Describes a column to be displayed on a Grid.
 * <p>
 * A Column uses it's associated mapping function to get the value to be displayed on each row of it's associated Grid.
 *
 *
 */
public final class Column<T> extends Element
{

	private Object head;
	private Object foot;
	private Function<T, Object> body = e -> e;
	private BiFunction<T, Style, Style> styler = (object, style) -> style;
	private List<ConditionalStyle> conditionalStyles = new ArrayList();

	public Column()
	{
		super(new Style().left());
	}

	/**
	 * Specify a value to be displayed on the column header.
	 *
	 * @param value the value to be displayed on the column header
	 *
	 * @return this, for chained invocations
	 */
	public Column<T> head(Object value)
	{
		this.head = value;
		return this;
	}

	/**
	 * Specify a value to be displayed on the column cells.
	 *
	 * @param value the function to be used to extract the value to be displayed on each cell of the column
	 *
	 * @return this, for chained invocations
	 */
	public Column<T> body(Function<T, Object> value)
	{
		Objects.requireNonNull(value);
		this.body = value;
		return this;
	}

	/**
	 * Specify a value to be displayed on the column footer.
	 *
	 * @param value the value to be displayed on the column footer
	 *
	 * @return this, for chained invocations
	 */
	public Column<T> foot(Object value)
	{
		this.foot = value;
		return this;
	}

	/**
	 * Defines a function to dynamically change the column style for each row of the Grid.
	 *
	 * @param styler a mapping function that returns the style to be applied for each row of the Grid
	 *
	 * @return this, for chained invocations
	 */
	public Column<T> styler(BiFunction<T, Style, Style> styler)
	{
		Objects.requireNonNull(styler);
		this.styler = styler;
		return this;
	}

	/**
	 * Returns the function to dynamically change the column style for each row of the Grid.
	 *
	 * @return the function to dynamically change the column style for each row of the Grid.
	 */
	public BiFunction<T, Style, Style> getStyler()
	{
		return styler;
	}

	public Column conditionalStyles(List<ConditionalStyle> conditionalStyles)
	{
		this.conditionalStyles = Objects.requireNonNull(conditionalStyles);
		return this;
	}

	public Column conditionalStyle(ConditionalStyle conditionalStyle)
	{
		this.conditionalStyles.add(conditionalStyle);
		return this;
	}

	public Column conditionalStyle(Predicate<Object> predicate, String style)
	{
		return this.conditionalStyle(new ConditionalStyle((Predicate<Object>) predicate, style));
	}

	/**
	 * Compute the column style para the specific object and value.
	 *
	 * @param object the object whose style must be computed
	 * @param value the value whose style must be computed
	 *
	 * @return the computed style.
	 */
	public Style computeStyle(T object, Object value)
	{
		Style style = new Style(style());

		gate.annotation.Color.Extractor
				.extract(value)
				.map(Color::of)
				.ifPresent(style::color);

		styler.apply(object, style);

		for (var conditionalStyle : conditionalStyles)
			if (conditionalStyle.getPredicate().test(value))
				style = style.apply(conditionalStyle.getStyle());

		return style;
	}

	/**
	 * Gets the current column header value.
	 *
	 * @return the current column header value
	 */
	public Object getHead()
	{
		return head;
	}

	/**
	 * Gets the column mapping function.
	 *
	 * @return the current column mapping function
	 */
	public Function<T, Object> getBody()
	{
		return body;
	}

	/**
	 * Gets the current column footer value.
	 *
	 * @return the current column footer value
	 */
	public Object getFoot()
	{
		return foot;
	}

	@Override
	public Column<T> style(Style style)
	{
		return (Column<T>) super.style(style);
	}

	public static Column of(JsonObject jsonObject)
	{

		var column = new Column();

		var head = jsonObject.get("head");
		if (head instanceof JsonString jsonString)
			column.head(jsonString.getValue());
		else if (head instanceof JsonNumber jsonNumber)
			column.head(jsonNumber.toString());
		else if (head instanceof JsonBoolean jsonBoolean)
			column.head(jsonBoolean.toString());
		else if (head != null)
			throw new IllegalArgumentException("Invalid column header");

		var style = jsonObject.get("style");
		if (style instanceof JsonString string)
			column.style(Style.of(string));
		else if (style instanceof JsonObject object)
			column.style(Style.of(object));
		else if (style != null)
			throw new IllegalArgumentException("Invalid column style");

		column.conditionalStyles(jsonObject.getJsonArray("conditions")
				.map(array -> array.stream()
				.filter(e -> e instanceof JsonObject)
				.map(e -> (JsonObject) e)
				.map(ConditionalStyle::of)
				.toList())
				.orElseGet(ArrayList::new));

		var property = jsonObject.get("property");
		if (property instanceof JsonString string)
			column.body(e ->
			{
				if (e instanceof JsonCollection jsonCollection)
					return jsonCollection.getProperty(string.getValue())
							.filter(value -> value instanceof JsonScalar)
							.map(value -> (JsonScalar) value)
							.map(JsonScalar::getScalarValue)
							.orElse("");
				else
					throw new IllegalArgumentException("Invalid column property");
			});
		else if (property != null)
			throw new IllegalArgumentException("Invalid column property");
		else
			throw new IllegalArgumentException("Unspecified column property");

		return column;
	}
}
