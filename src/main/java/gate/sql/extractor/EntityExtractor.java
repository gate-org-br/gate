package gate.sql.extractor;

import gate.lang.property.Property;
import gate.sql.Cursor;
import gate.sql.CursorSpliterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts each row from a Cursor as a stream of java objects of the specified type with their properties set to their respective column values.
 *
 * 
 */
public class EntityExtractor<T> implements Extractor<T>
{

	private final Class<T> type;

	/**
	 * Creates a new EntityExtractor for the specified java type.
	 *
	 * @param type the java type of the objects to be extracted
	 */
	public EntityExtractor(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Extracts each row from the specified Cursor as a stream of java objects of the specified type with their properties set to their respective column
	 * values.
	 *
	 * @param cursor the Cursor from with the objects will be extracted
	 *
	 * @return a stream of java objects of the specified type with their properties set to their respective column values
	 */
	@Override
	public Stream<T> extract(Cursor cursor)
	{
		return StreamSupport.stream(new CursorSpliterator<T>()
		{
			private final List<Property> properties = cursor.getProperties(type);

			@Override
			public boolean tryAdvance(Consumer<? super T> action)
			{
				if (!cursor.next())
					return false;
				action.accept(cursor.getEntity(type, properties));
				return true;
			}

		}, false);
	}

}
