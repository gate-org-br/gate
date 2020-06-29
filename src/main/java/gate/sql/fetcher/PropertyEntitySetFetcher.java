package gate.sql.fetcher;

import gate.error.AppError;
import gate.lang.property.Property;
import gate.sql.Cursor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PropertyEntitySetFetcher<T> implements Fetcher<Set<T>>
{

	private final Class<T> type;
	private final List<Property> properties;
	private final Set<T> result = new LinkedHashSet<>();

	public PropertyEntitySetFetcher(Class<T> type, List<Property> properties)
	{
		this.type = type;
		this.properties = properties;
	}

	public PropertyEntitySetFetcher(Class<T> type, String[] properties)
	{
		this.type = type;
		this.properties = Property.getProperties(type, properties);
	}

	@Override
	public Set<T> fetch(Cursor rs)
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
			| SecurityException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new AppError(e);
		}
	}

	public Set<T> getResult()
	{
		return result;
	}
}
