package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.type.Hierarchy;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Fetches all rows as hierarchical entities and assembles their relationships.
 *
 * @param <T> entity type
 */
public class HierarchyListFetcher<T extends Hierarchy<T>> implements Fetcher<List<T>>
{

	private final Class<T> type;
	private final Function<String, Object> context;

	/**
	 * Creates a new hierarchy list fetcher for the specified type.
	 *
	 * @param type entity type to be fetched
	 */
	public HierarchyListFetcher(Class<T> type)
	{
		this(type, ignore -> null);
	}

	/**
	 * Creates a new hierarchy list fetcher for the specified type.
	 *
	 * <p>The {@code context} may provide a value for a property, identified by its full name,
	 * before the cursor tries to read it from the current row. Returning {@code null} means
	 * the property should be resolved normally from the cursor.</p>
	 *
	 * @param type    entity type to be fetched
	 * @param context function used to provide contextual values for specific property names
	 */
	public HierarchyListFetcher(Class<T> type, Function<String, Object> context)
	{
		this.type = Objects.requireNonNull(type);
		this.context = Objects.requireNonNull(context);
	}

	/**
	 * Fetches all rows as hierarchical entities.
	 *
	 * @param cursor cursor to be fetched
	 * @return root entities of the assembled hierarchy
	 */
	@Override
	public List<T> fetch(Cursor cursor)
	{
		var list = new EntityListFetcher<>(type, context).fetch(cursor);
		return Hierarchy.setup(list);
	}
}
