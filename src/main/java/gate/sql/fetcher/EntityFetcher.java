package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.AppError;
import gate.lang.property.Property;
import java.util.Optional;

/**
 * Fetches first row from a Cursor as a java object of the specified type with it's properties set to their respective
 * column values.
 *
 * @param <T> type of the object to be fetched
 */
public class EntityFetcher<T> implements Fetcher<Optional<T>>
{

	private final Class<T> type;

	/**
	 * Creates a new EntityFetcher for the specified java type.
	 *
	 * @param type the java type to be fetched
	 */
	public EntityFetcher(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Fetches first row from the specified Cursor as a java object of the specified type with it's properties set to
	 * their respective column values.
	 *
	 * @param cursor the Cursor from with the object will be fetched
	 *
	 * @return an Optional with the first row or the specified Cursor as a java object of the specified type with it's
	 *         properties set to their respective column values or an empty Optional if the Cursor is empty
	 */
	@Override
	public Optional<T> fetch(Cursor cursor)
	{
		try
		{
			if (!cursor.next())
				return Optional.empty();

			T result = type.newInstance();
			cursor.getPropertyNames(type)
					.stream().map(e -> Property.getProperty(type, e))
					.forEach(e -> e.setValue(result, cursor.getCurrentValue(e.getRawType())));
			return Optional.of(result);
		} catch (IllegalAccessException | InstantiationException e)
		{
			throw new AppError(e);
		}
	}
}
