package gate.sql.fetcher;

import gate.lang.property.Property;
import gate.sql.Cursor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Fetches first row from a Cursor as a java object of the specified type with it's properties set to their respective column values.
 *
 *
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
	 * Fetches first row from the specified Cursor as a java object of the specified type with it's properties set to their respective column values.
	 *
	 * @param cursor the Cursor from with the object will be fetched
	 *
	 * @return an Optional with the first row or the specified Cursor as a java object of the specified type with it's properties set to their respective
	 * column values or an empty Optional if the Cursor is empty
	 */
	@Override
	public Optional<T> fetch(Cursor cursor)
	{
		try
		{
			if (!cursor.next())
				return Optional.empty();

			T result = type.getDeclaredConstructor().newInstance();
			cursor.getPropertyNames(type)
				.stream().map(e -> Property.getProperty(type, e))
				.forEach(e ->
				{
					try
					{
						Class<?> clazz = e.getRawType();
						if (clazz == boolean.class)
							e.setBoolean(result, cursor.getCurrentBooleanValue());
						else if (clazz == char.class)
							e.setChar(result, cursor.getCurrentCharValue());
						else if (clazz == byte.class)
							e.setByte(result, cursor.getCurrentByteValue());
						else if (clazz == short.class)
							e.setShort(result, cursor.getCurrentShortValue());
						else if (clazz == int.class)
							e.setInt(result, cursor.getCurrentIntValue());
						else if (clazz == long.class)
							e.setLong(result, cursor.getCurrentLongValue());
						else if (clazz == float.class)
							e.setFloat(result, cursor.getCurrentFloatValue());
						else if (clazz == double.class)
							e.setDouble(result, cursor.getCurrentDoubleValue());
						else
							e.setValue(result, cursor.getCurrentValue(clazz));
					} catch (Exception ex)
					{
						throw new UnsupportedOperationException("Error trying to process property "
							+ e + ": " + ex.getMessage(), ex);
					}
				});
			return Optional.of(result);
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}
}
