package gate.sql.fetcher;

import gate.lang.property.Property;
import gate.sql.Cursor;
import gate.util.Page;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a cursor as a list of java objects of the specified type with it's properties set to their respective column values.
 *
 *
 * @param <T> the java type to be fetched
 */
public class EntityPageFetcher<T> implements Fetcher<Page<T>>
{

	private final int pageSize;
	private final int pageIndx;
	private final Class<T> type;

	/**
	 * Creates a new EntityListFetcher for the specified java type.
	 *
	 * @param type the java type to be fetched
	 * @param pageSize number of entities per page
	 * @param pageIndx index of the page
	 */
	public EntityPageFetcher(Class<T> type, int pageSize, int pageIndx)
	{
		this.type = type;
		this.pageSize = pageSize;
		this.pageIndx = pageIndx;
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
	public Page<T> fetch(Cursor cursor)
	{
		try
		{

			if (!cursor.next())
				return Page.of(List.of(), 0, pageSize, pageIndx);

			List<T> result = new ArrayList<>();
			List<String> names = cursor.getPropertyNames(type);
			if (!names.contains("dataSize"))
				throw new UnsupportedOperationException("Result set does not contain a dataSize column");
			names.removeIf(e -> e.equals("dataSize"));
			List<Property> properties = Property.getProperties(type, names);

			int dataSize = cursor.getIntValue("dataSize");

			do
			{

				T entity = type.getDeclaredConstructor().newInstance();
				properties.forEach(e ->
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
				});
				result.add(entity);
			} while (cursor.next());

			return Page.of(result, dataSize, pageSize, pageIndx);

		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException
			| SecurityException | IllegalArgumentException | InvocationTargetException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}
}
