package gate.sql.fetcher;

import gate.lang.property.Property;
import gate.sql.Cursor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fetches a cursor as a list of java objects of the specified type with it's properties set to their respective column values.
 */
public class EntityListFetcher<T> implements Fetcher<List<T>>
{

	private final Class<T> type;
	private final List<T> result = new ArrayList<>();

	/**
	 * Creates a new EntityListFetcher for the specified java type.
	 *
	 * @param type the java type to be fetched
	 */
	public EntityListFetcher(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Fetches each row from the specified Cursor as a list of java objects of the specified type with it's properties set to their respective column
	 * values.
	 *
	 * @param cursor the Cursor to be fetched
	 * @return a List with each row or the specified Cursor as a java object of the specified type with it's properties set to their respective column
	 * values
	 */
	@Override
	public List<T> fetch(Cursor cursor)
	{
		var graph = cursor.getPropertyGraph(type);
		while (cursor.next())
			result.add(cursor.getEntity(graph));
		return result;
	}

	/**
	 * Return the accumulated result of fetch operations.
	 *
	 * @return the accumulated result of fetch operations
	 */
	public List<T> getResult()
	{
		return result;
	}
}
