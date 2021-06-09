package gate.report;

import gate.type.Color;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

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
}
