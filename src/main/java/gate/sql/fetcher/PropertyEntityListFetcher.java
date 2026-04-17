package gate.sql.fetcher;

import gate.error.InternalServerException;
import gate.lang.property.Property;
import gate.sql.Cursor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PropertyEntityListFetcher<T> implements Fetcher<List<T>>
{

	private final Class<T> type;
	private final List<Property> properties;
	private final List<T> result = new ArrayList<>();

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

			while (rs.next())
			{
				T entity = type.getDeclaredConstructor().newInstance();
				properties.forEach(e -> e.setValue(entity, rs.getValue(e.getRawType(), e.toString())));
				result.add(entity);
			}
			return result;
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException
		         | SecurityException | IllegalArgumentException | InvocationTargetException ex)
		{
			throw new InternalServerException(ex.getMessage());
		}
	}

	public List<T> getResult()
	{
		return result;
	}


}