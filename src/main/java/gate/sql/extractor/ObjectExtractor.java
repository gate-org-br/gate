package gate.sql.extractor;

import gate.sql.Cursor;
import gate.sql.CursorSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts each row from a Cursor as stream of java objects.
 */
public class ObjectExtractor implements Extractor<Object>
{

	/**
	 * Extract each row of the specified cursor as stream of java objects.
	 *
	 * @return each row of the specified cursor as stream of java objects
	 */
	@Override
	public Stream<Object> extract(Cursor cursor)
	{
		return StreamSupport.stream(new CursorSpliterator<Object>()
		{
			private final Class<?> type = cursor.getColumnTypes()[0];

			@Override
			public boolean tryAdvance(Consumer<? super Object> action)
			{
				if (!cursor.next())
					return false;

				action.accept(cursor.getValue(type, 1));

				return true;
			}

		}, false);
	}

}
