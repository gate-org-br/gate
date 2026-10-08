package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.util.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Fetches the current cursor as a {@link Page} of entities.
 *
 * <p>The cursor must expose a {@code dataSize} column containing the total number of rows that
 * match the filter, ignoring pagination. This fetcher does not run a separate count query; it
 * reads that value directly from the current result set.</p>
 *
 * <p>Each row is materialized through {@link Cursor#getEntity(gate.lang.property.PropertyGraph)},
 * so paged entity fetching follows the same construction rules as the rest of the framework.</p>
 *
 * @param <T> the Java type to be fetched
 */
public class EntityPageFetcher<T> implements Fetcher<Page<T>>
{

	private final int pageSize;
	private final int pageIndx;
	private final Class<T> type;
	private final Function<String, Object> context;

	/**
	 * Creates a new paged entity fetcher for the specified Java type.
	 *
	 * @param type the Java type to be fetched
	 * @param pageSize number of entities per page
	 * @param pageIndx index of the page
	 */
	public EntityPageFetcher(Class<T> type, int pageSize, int pageIndx)
	{
		this.type = type;
		this.pageSize = pageSize;
		this.pageIndx = pageIndx;
		this.context = ignore -> null;
	}

	/**
	 * Creates a new paged entity fetcher for the specified Java type.
	 *
	 * <p>The {@code context} may provide a value for a property, identified by its full name,
	 * before the cursor tries to read it from the current row. Returning {@code null} means
	 * the property should be resolved
	 * normally from the cursor.</p>
	 *
	 * @param type the Java type to be fetched
	 * @param pageSize number of entities per page
	 * @param pageIndx index of the page
	 * @param context function used to provide contextual values for specific property names
	 */
	public EntityPageFetcher(Class<T> type, int pageSize, int pageIndx, Function<String, Object> context)
	{
		this.type = type;
		this.pageSize = pageSize;
		this.pageIndx = pageIndx;
		this.context = context;
	}

	/**
	 * Fetches the current cursor as a page of entities.
	 *
	 * <p>The result set must contain a {@code dataSize} column. If it does not, this method fails
	 * with {@link UnsupportedOperationException}.</p>
	 *
	 * @param cursor the cursor to be fetched
	 * @return a page containing the current rows and the total size informed by {@code dataSize}
	 * @throws IllegalStateException if the cursor cannot be read or the entities cannot be materialized
	 */
	@Override
	public Page<T> fetch(Cursor cursor)
	{
		try
		{

			if (!cursor.next())
				return Page.of(List.of(), 0, pageSize, pageIndx);

			List<T> result = new ArrayList<>();
			List<String> names = cursor.getPropertyNames();
			if (!names.contains("dataSize"))
				throw new UnsupportedOperationException("Result set does not contain a dataSize column");
			names.removeIf(e -> e.equals("dataSize"));
			var graph = cursor.getPropertyGraph(type);

			int dataSize = cursor.getIntValue("dataSize");

			do
				result.add(cursor.getEntity(graph, context));
			while (cursor.next());

			return Page.of(result, dataSize, pageSize, pageIndx);

		} catch (RuntimeException ex)
		{
			throw new IllegalStateException("Failed to fetch entity page from cursor", ex);
		}
	}
}
