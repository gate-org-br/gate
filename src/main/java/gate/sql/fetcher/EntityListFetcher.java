package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.AppError;
import gate.lang.property.Property;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a cursor as a list of java objects of the specified type with it's properties set to their respective column
 * values.
 *
 * @param <T> type returned by the Fetcher
 *
 * @author Davi Nunes da Silva
 */
public class EntityListFetcher<T> implements Fetcher<List<T>>
{

	private final Class<T> type;

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
	 * Fetches each row from the specified Cursor as a list of java objects of the specified type with it's properties
	 * set to their respective column values.
	 *
	 * @param cursor the Cursor to be fetched
	 *
	 * @return a List with each row or the specified Cursor as a java object of the specified type with it's
	 *         properties set to their respective column values
	 */
	@Override
	public List<T> fetch(Cursor cursor)
	{
		try
		{
			List<T> results = new ArrayList<>();
			List<Property> properties
					= Property.getProperties(type, cursor.getPropertyNames(type));

			while (cursor.next())
			{
				T result = type.newInstance();
				properties.forEach(e -> e.setValue(result, cursor.getValue(e.getRawType(), e.toString())));
				results.add(result);
			}
			return results;

		} catch (IllegalAccessException | InstantiationException e)
		{
			throw new AppError(e);
		}

	}
}
