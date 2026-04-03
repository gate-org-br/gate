package gate.sql.fetcher;

import gate.sql.Cursor;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fetches all rows as a set of entities of the specified type.
 *
 * @param <T> entity type
 */
public class EntitySetFetcher<T> implements Fetcher<Set<T>>
{

    private final Class<T> type;
    private final Set<T> result = new LinkedHashSet<>();

    /**
     * Creates a new entity set fetcher for the specified type.
     *
     * @param type entity type to be fetched
     */
    public EntitySetFetcher(Class<T> type)
    {
        this.type = type;
    }

    /**
     * Fetches all rows as entities.
     *
     * @param cursor cursor to be fetched
     * @return set of fetched entities
     */
	@Override
	public Set<T> fetch(Cursor cursor)
	{
		var graph = cursor.getPropertyGraph(type);
		while (cursor.next())
			result.add(cursor.getEntity(graph));
		return result;
	}

    /**
     * Returns the accumulated result of fetch operations.
     *
     * @return accumulated result
     */
    public Set<T> getResult()
    {
        return result;
    }
}
