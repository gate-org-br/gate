package gate.sql.fetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import gate.sql.Cursor;

/**
 * Fetches all rows as entities of the specified type.
 *
 * @param <T> entity type
 */
public class EntityListFetcher<T> implements Fetcher<List<T>>
{

	private final Class<T> type;
	private final Function<String, Object> context;
	private final List<T> result = new ArrayList<>();

	/**
	 * Creates a new entity list fetcher for the specified type.
	 *
	 * @param type entity type to be fetched
	 */
	public EntityListFetcher(Class<T> type)
	{
		this.type = type;
		this.context = ignore -> null;
	}

	/**
	 * Creates a new entity list fetcher for the specified type.
	 *
	 * <p>The {@code context} may provide a value for a property, identified by its full name,
	 * before the cursor tries to read it from the current row. Returning {@code null} means
	 * the property should be resolved
	 * normally from the cursor.</p>
	 *
	 * @param type entity type to be fetched
	 * @param context function used to provide contextual values for specific property names
	 */
	public EntityListFetcher(Class<T> type, Function<String, Object> context)
	{
		this.type = type;
		this.context = context;
	}

	/**
	 * Fetches all rows as entities.
	 *
	 * @param cursor cursor to be fetched
	 * @return list of fetched entities
	 */
	@Override
	public List<T> fetch(Cursor cursor)
	{
		var graph = cursor.getPropertyGraph(type);
		while (cursor.next())
			result.add(cursor.getEntity(graph, context));
		return result;
	}

	/**
	 * Returns the accumulated result of fetch operations.
	 *
	 * @return accumulated result
	 */
	public List<T> getResult()
	{
		return result;
	}
}
