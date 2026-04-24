package gate.sql.fetcher;

import gate.lang.property.Property;
import gate.lang.property.PropertyGraph;
import gate.sql.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Fetches each row as an entity built only from the specified properties.
 *
 * @param <T> the Java type to be fetched
 */
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
			var graph = PropertyGraph.of(type, properties.stream().map(Property::toString).toList());
			while (rs.next())
				result.add(rs.getEntity(graph));
			return result;
		} catch (RuntimeException e)
		{
			throw new IllegalStateException("Failed to fetch partial entity list from cursor", e);
		}
	}

	public List<T> getResult()
	{
		return result;
	}
}
