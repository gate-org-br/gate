package gate.report;

import gate.converter.Converter;
import gate.language.Language;
import gate.type.DataGrid;
import gate.type.PivotTable;
import gate.util.Toolkit;

import java.util.*;
import java.util.function.Function;

/**
 * Represents a grid on a report.
 * <p>
 * A grid is associated with a data source from where it obtains the values to be displayed on each of it's cells. Each object of the associated data source
 * will generate a single row on the Grid. The value to be displayed on each column is obtained using a mapping function on it's row value.
 *
 *
 *
 * @author davins
 */
public class Grid<T> extends ReportElement
{

	private Integer limit;
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
	 * Each column is associated with a mapping function used to get the value to be displayed for each row of the Grid.
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
	 * Sets a value to be displayed on the grid's caption using a java Formatter.
	 *
	 * @param caption the format string of the caption to be passed to the java Formatter
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
	 * Sets a mapping function to select a value to be displayed on a sub grid of the current grid.
	 *
	 * @param children the function where to get the values to be displayed on the sub grid
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

	@Override
	public Element compact()
	{
		Iterator<Column<T>> iterator = columns.iterator();
		while (iterator.hasNext())
		{
			Column<T> column = iterator.next();
			if (Toolkit.collection(datasource)
				.stream()
				.map(e -> column.getBody().apply((T) e))
				.allMatch(e -> e == null))
				iterator.remove();
		}

		return this;
	}

	/**
	 * Sets the max number of columns to be shown on doc types of limited size.
	 *
	 * @param limit the max number of columns to be shown on doc types of limited size
	 *
	 * @return the same object, for chained invocations
	 */
	public Grid<T> setLimit(Integer limit)
	{
		if (limit != null && limit < 0)
			throw new IllegalArgumentException("limit");
		this.limit = limit;
		return this;
	}

	/**
	 * Returns the max number of columns to be shown on doc types of limited size.
	 *
	 * @return the max number of columns to be shown on doc types of limited size.
	 */
	public Integer getLimit()
	{
		return limit;
	}

	@Override
	public boolean isEmpty()
	{
		return columns.isEmpty();
	}

	public static Grid<List<Object>> of(String caption, PivotTable dataset)
	{
		Grid<List<Object>> grid = new Grid<>(dataset.values())
			.setCaption(caption)
			.add(new Column<List<Object>>()
				.head(dataset.header().get(0))
				.body(e -> Language.PORTUGUESE.capitalize(Converter.toString(e.get(0))))
				.style(new Style().width(90).left()));

		for (int i = 1; i < dataset.header().size(); i++)
		{
			var index = i;
			grid.add(new Column<List<Object>>().head(dataset.header().get(i))
				.body(e -> e.get(index)).style(new Style().width(10)));
		}

		return grid;
	}

	public static Grid<Object[]> of(String caption, DataGrid dataset)
	{
		Grid<Object[]> grid = new Grid<>(dataset)
			.setCaption(caption)
			.add(new Column<Object[]>()
				.head(dataset.getHead()[0])
				.body(e -> Converter.toText(e[0]))
				.style(new Style().width(90).left()));

		for (int i = 1; i < dataset.getHead().length; i++)
		{
			var index = i;
			grid.add(new Column<Object[]>().head(dataset.getHead()[i])
				.body(e -> Converter.toText(e[index]))
				.style(new Style().width(10)));
		}

		return grid;
	}
}
