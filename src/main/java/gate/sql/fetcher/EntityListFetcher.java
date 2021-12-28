package gate.sql.fetcher;

import gate.lang.property.Property;
import gate.sql.Cursor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a cursor as a list of java objects of the specified type with it's properties set to their respective column values.
 *
 *
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
	 *
	 * @return a List with each row or the specified Cursor as a java object of the specified type with it's properties set to their respective column
	 * values
	 */
	@Override
	public List<T> fetch(Cursor cursor)
	{
		try
		{
			List<Property> properties
				= Property.getProperties(type, cursor.getPropertyNames(type));

			while (cursor.next())
			{
				T entity = type.getDeclaredConstructor().newInstance();
				properties.forEach(e ->
				{
					try
					{
						Class<?> clazz = e.getRawType();
						if (clazz == boolean.class)
							e.setBoolean(entity, cursor.getCurrentBooleanValue());
						else if (clazz == char.class)
							e.setChar(entity, cursor.getCurrentCharValue());
						else if (clazz == byte.class)
							e.setByte(entity, cursor.getCurrentByteValue());
						else if (clazz == short.class)
							e.setShort(entity, cursor.getCurrentShortValue());
						else if (clazz == int.class)
							e.setInt(entity, cursor.getCurrentIntValue());
						else if (clazz == long.class)
							e.setLong(entity, cursor.getCurrentLongValue());
						else if (clazz == float.class)
							e.setFloat(entity, cursor.getCurrentFloatValue());
						else if (clazz == double.class)
							e.setDouble(entity, cursor.getCurrentDoubleValue());
						else
							e.setValue(entity, cursor.getCurrentValue(clazz));
					} catch (Exception ex)
					{
						throw new UnsupportedOperationException("Error trying to process property "
							+ e + ": " + ex.getMessage(), ex);
					}
				});
				result.add(entity);
			}
			return result;

		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException
			| SecurityException | IllegalArgumentException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
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
