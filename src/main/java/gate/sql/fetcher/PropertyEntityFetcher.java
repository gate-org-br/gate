package gate.sql.fetcher;

import gate.error.AppError;
import gate.lang.property.Property;
import gate.sql.Cursor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PropertyEntityFetcher<T> implements Fetcher<T>
{

	private final Class<T> type;
	private final List<Property> properties;

	public PropertyEntityFetcher(Class<T> type, List<Property> properties)
	{
		this.type = type;
		this.properties = properties;
	}

	public PropertyEntityFetcher(Class<T> type, String[] properties)
	{
		this.type = type;
		this.properties = Property.getProperties(type, properties);
	}

	@Override
	public T fetch(Cursor cursor)
	{
		try
		{
			if (cursor.next())
			{
				T result = type.getDeclaredConstructor().newInstance();
				properties.forEach(e -> e.setValue(result, cursor.getValue(e.getRawType(), e.toString())));
				return result;
			}
			return null;
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException ex)
		{
			throw new AppError(ex);
		}
	}
}
