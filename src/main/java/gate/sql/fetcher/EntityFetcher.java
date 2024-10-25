package gate.sql.fetcher;

import gate.lang.property.PropertyGraph;
import gate.sql.Cursor;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.Optional;

/**
 * Fetches first row from a Cursor as a java object of the specified type with its properties set to their respective column values.
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
	 * Fetches first row from the specified Cursor as a java object of the specified type with its properties set to their respective column values.
	 *
	 * @param cursor the Cursor from with the object will be fetched
	 * @return an Optional with the first row or the specified Cursor as a java object of the specified type with its properties set to their respective
	 * column values or an empty Optional if the Cursor is empty
	 */
	@Override
	public Optional<T> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();
		return Optional.of(cursor.getEntity(type));
	}
}
