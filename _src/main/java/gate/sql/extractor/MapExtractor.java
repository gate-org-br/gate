package gate.sql.extractor;

import gate.sql.Cursor;
import gate.sql.CursorSpliterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts each row from a Cursor as stream of maps whose keys are the column names and values are the column values.
 */
public class MapExtractor implements Extractor<Map<String, Object>>
{

	/**
	 * Extract each row of the specified cursor as stream of maps whose keys are the column names and values are the column values.
	 *
	 * @return each row of the specified cursor as stream of maps whose keys are the column names and values are the column values
	 */
	@Override
	public Stream<Map<String, Object>> extract(Cursor cursor)
	{
		return StreamSupport.stream(new CursorSpliterator<Map<String, Object>>()
		{
			private final Map<String, Class<?>> metadata = cursor.getMetaData();

			@Override
			public boolean tryAdvance(Consumer<? super Map<String, Object>> action)
			{
				if (!cursor.next())
					return false;
				action.accept(metadata.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey(), e -> cursor.getValue(e.getValue(), e.getKey()))));
				return true;
			}

		}, false);
	}

}
