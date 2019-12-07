package gate.sql.extractor;

import gate.sql.Cursor;
import gate.sql.CursorSpliterator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts each row from a Cursor as stream of maps whose keys are the column names and values are the column values.
 */
public class TypedMapExtractor implements Extractor<Map<String, Object>>
{

	private final Class[] types;

	public TypedMapExtractor(Class[] types)
	{
		this.types = types;
	}

	/**
	 * Extract each row of the specified cursor as stream of maps whose keys are the column names and values are the column values as objects of the
	 * specified types.
	 *
	 * @return each row of the specified cursor as stream of maps whose keys are the column names and values are the column values as objects of the
	 * specified types
	 */
	@Override
	public Stream<Map<String, Object>> extract(Cursor cursor)
	{
		return StreamSupport.stream(new CursorSpliterator<Map<String, Object>>()
		{
			private final String[] names = cursor.getColumnNames();

			@Override
			public boolean tryAdvance(Consumer<? super Map<String, Object>> action)
			{
				if (!cursor.next())
					return false;

				Map<String, Object> result = new HashMap<>();
				for (int i = 0; i < names.length; i++)
					result.put(names[i], cursor.getValue(types[i], names[i]));
				action.accept(result);
				return true;
			}

		}, false);
	}

}
