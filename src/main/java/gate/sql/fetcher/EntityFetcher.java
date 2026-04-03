package gate.sql.fetcher;

import gate.sql.Cursor;

import java.util.Optional;
import java.util.function.Function;

/**
 * Fetches the first row as an entity of the specified type.
 *
 * @param <T> entity type
 */
public class EntityFetcher<T> implements Fetcher<Optional<T>>
{

	private final Class<T> type;
	private final Function<String, Object> context;

	/**
	 * Creates a new entity fetcher for the specified type.
	 *
	 * @param type entity type to be fetched
	 */
	public EntityFetcher(Class<T> type)
	{
		this.type = type;
		this.context = e -> null;
	}

	/**
	 * Creates a new entity fetcher for the specified type.
	 *
	 * @param type entity type to be fetched
	 * @param context function used to provide contextual values for specific property names
	 *
	 * <p>The {@code context} may provide a value for a property, identified by its full name,
	 * before the cursor tries to read it from the current row. Returning {@code null} means
	 * the property should be resolved
	 * normally from the cursor.</p>
	 */
	public EntityFetcher(Class<T> type, Function<String, Object> context)
	{
		this.type = type;
		this.context = context;
	}

	/**
	 * Fetches the first row as an entity.
	 *
	 * @param cursor cursor to be fetched
	 * @return optional containing the first entity, or empty if the cursor has no rows
	 */
	@Override
	public Optional<T> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();
		return Optional.of(cursor.getEntity(type, context));
	}
}
