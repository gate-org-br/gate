package gate.sql.fetcher;

import gate.lang.property.Property;
import gate.lang.property.PropertyGraph;
import gate.sql.Cursor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Fetches each row as an entity built only from the specified properties.
 *
 * @param <T> the Java type to be fetched
 */
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
			var graph = PropertyGraph.of(type, properties.stream().map(Property::toString).toList());
			while (rs.next())
				result.add(rs.getEntity(graph));
			return result;
		} catch (RuntimeException e)
		{
			throw new IllegalStateException("Failed to fetch partial entity set from cursor", e);
		}
	}

	public Set<T> getResult()
	{
		return result;
	}
}
