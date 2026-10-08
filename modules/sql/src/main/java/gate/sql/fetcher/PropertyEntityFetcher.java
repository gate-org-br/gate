package gate.sql.fetcher;

import gate.lang.property.Property;
import gate.lang.property.PropertyGraph;
import gate.sql.Cursor;

import java.util.List;
import java.util.Optional;

/**
 * Fetches the first row as an entity built only from the specified properties.
 *
 * @param <T> the Java type to be fetched
 */
public class PropertyEntityFetcher<T> implements Fetcher<Optional<T>>
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
	public Optional<T> fetch(Cursor cursor)
	{
		try
		{
			var graph = PropertyGraph.of(type, properties.stream().map(Property::toString).toList());
			if (cursor.next())
				return Optional.of(cursor.getEntity(graph));
			return Optional.empty();
		} catch (RuntimeException ex)
		{
			throw new IllegalStateException("Failed to fetch partial entity from cursor", ex);
		}
	}
}
