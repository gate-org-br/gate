package gate.type;

import gate.lang.json.JsonArray;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataGrid extends ArrayList<Object[]>
{

	private static final long serialVersionUID = 1L;

	public String[] head;
	public Object[] foot;

	public DataGrid(String... head)
	{
		this.head = head;
	}

	public DataGrid(String[] head, Object... foot)
	{
		this.head = head;
		this.foot = foot;
	}

	public void setHead(String[] head)
	{
		this.head = head;
	}

	public void setFoot(Object[] foot)
	{
		this.foot = foot;
	}

	public String[] getHead()
	{
		return head;
	}

	public Object[] getFoot()
	{
		return foot;
	}

	/**
	 * Insert a new row into the DataGrid.
	 *
	 * @param objects the values to be inserted
	 *
	 * @return the same object, for chained invocations
	 */
	public DataGrid insert(Object... objects)
	{
		add(objects);
		return this;
	}

	/**
	 * Creates a new DataGrid with the specified columns.
	 *
	 * @param indexes the indexes of the columns to be selected
	 *
	 * @return a new DataGrid with the specified columns
	 */
	public DataGrid select(int... indexes)
	{
		DataGrid dataGrid
			= foot != null ? new DataGrid(IntStream.of(indexes)
					.mapToObj(e -> head[e])
					.toArray(String[]::new),
					IntStream.of(indexes)
						.mapToObj(e -> foot[e])
						.toArray())
				: new DataGrid(IntStream.of(indexes)
					.mapToObj(e -> head[e])
					.toArray(String[]::new));

		stream().map(values -> IntStream.of(indexes).mapToObj(e -> values[e])
			.toArray()).collect(Collectors.toCollection(() -> dataGrid));

		return dataGrid;
	}

	public String toString(int... indexes)
	{
		return select(indexes).toString();
	}

	public String toString(List<Number> indexes)
	{
		return select(indexes.stream().mapToInt(Number::intValue).toArray()).toString();
	}

	@Override
	public String toString()
	{
		return JsonArray.format(Stream.concat(Stream.of((Object) head), stream())).toString();
	}

	public DataGrid rollup()
	{
		if (size() > 0)
			setFoot(remove(size() - 1));
		return this;
	}
}
