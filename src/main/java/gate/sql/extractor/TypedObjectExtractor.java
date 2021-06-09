package gate.sql.extractor;

import gate.sql.Cursor;
import gate.sql.CursorSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts each row from a Cursor as stream of java objects of the specified type.
 *
 * 
 */
public class TypedObjectExtractor<T> implements Extractor<T>
{

	private final Class<T> type;

	public TypedObjectExtractor(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Extract each row of the specified cursor as stream of java objects of the specified type.
	 *
	 * @return each row of the specified cursor as stream of java objects of the specified type
	 */
	@Override
	public Stream<T> extract(Cursor cursor)
	{
		return StreamSupport.stream(new CursorSpliterator<T>()
		{
			@Override
			public boolean tryAdvance(Consumer<? super T> action)
			{
				if (!cursor.next())
					return false;

				action.accept(cursor.getValue(type, 1));

				return true;
			}

		}, false);
	}

}
