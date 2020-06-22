package gate.sql.fetcher;

import gate.error.AppError;
import gate.lang.property.Property;
import gate.sql.Cursor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PropertyEntityListFetcher<T> implements Fetcher<List<T>>
{

	private final Class<T> type;
	private final List<Property> properties;

	public PropertyEntityListFetcher(Class<T> type, List<Property> properties)
	{
		this.type = type;
		this.properties = properties;
	}

	public PropertyEntityListFetcher(Class<T> type, String[] properties)
	{
		this.type = type;
		this.properties = Property.getProperties(type, properties);
	}

	@Override
	public List<T> fetch(Cursor rs)
	{
		try
		{
			List<T> results = new ArrayList<>();
			while (rs.next())
			{
				T result = type.getDeclaredConstructor().newInstance();
				properties.forEach(e -> e.setValue(result, rs.getValue(e.getRawType(), e.toString())));
				results.add(result);
			}
			return results;
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new AppError(e);
		}
	}
}
