package gate.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a grid on a report.
 * <p>
 * A grid is associated with a data source from where it obtains the values to
 * be displayed on each of it's cells. Each object of the associated data source
 * will generate a single row on the Grid. The value to be displayed on each
 * column is obtained using a mapping function on it's row value.
 *
 * @param <T> type of the objects to be displayed by the grid
 *
 * @author davins
 */
public class Grid<T> extends ReportElement
{

	private String caption;
	private final Iterable<T> datasource;
	private Function<T, Object> children;
	private final List<Column<T>> columns = new ArrayList<>();

	public Grid(Iterable<T> datasource)
	{
		super(new Style());
		Objects.requireNonNull(datasource);
		this.datasource = datasource;
	}

	/**
	 * Adds a new Column to the Grid.
	 * <p>
	 * Each column is associated with a mapping function used to get the
	 * value to be displayed for each row of the Grid.
	 *
	 * @return the new Column added
	 */
	public final Column<T> add()
	{
		Column<T> column = new Column<>();
		columns.add(column);
		return column;
	}

	public final Grid<T> add(Column<T> column)
	{
		columns.add(column);
		return this;
	}

	/**
	 * Sets a value to be displayed on the grid's caption.
	 *
	 * @param caption the caption to be displayed on the grid
	 *
	 * @return this, for chained invocations
	 */
	public final Grid<T> setCaption(String caption)
	{
		this.caption = caption;
		return this;
	}

	/**
	 * Sets a value to be displayed on the grid's caption using a java
	 * Formatter.
	 *
	 * @param caption the format string of the caption to be passed to the
	 * java Formatter
	 * @param args the parameters to be passed to the java Formatter
	 *
	 * @return this, for chained invocations
	 * @see java.util.Formatter
	 */
	public final Grid<T> setCaption(String caption, Object... args)
	{
		this.caption = String.format(caption, args);
		return this;
	}

	/**
	 * Sets a mapping function to select a value to be displayed on a sub
	 * grid of the current grid.
	 *
	 * @param children the function where to get the values to be displayed
	 * on the sub grid
	 *
	 * @return the same object, for chained invocations
	 */
	public final Grid<T> setChildren(Function<T, Object> children)
	{
		this.children = children;
		return this;
	}

	public final String getCaption()
	{
		return caption;
	}

	public final Iterable<?> getData()
	{
		return datasource;
	}

	public final Function<T, Object> getChildren()
	{
		return children;
	}

	public final List<Column<T>> getColumns()
	{
		return Collections.unmodifiableList(columns);
	}

	@Override
	public final Grid<T> style(Style style)
	{
		return (Grid<T>) super.style(style);
	}
}
