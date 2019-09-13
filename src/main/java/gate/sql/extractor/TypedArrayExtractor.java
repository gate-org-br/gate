package gate.sql.extractor;

import gate.sql.Cursor;
import gate.sql.CursorSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts each row from a Cursor as stream of arrays.
 */
public class TypedArrayExtractor implements Extractor<Object[]>
{

	private final Class<?>[] types;

	public TypedArrayExtractor(Class<?>[] types)
	{
		this.types = types;
	}

	/**
	 * Extract each row of the specified cursor as stream of arrays of objects of the specified types.
	 *
	 * @return each row of the specified cursor as stream of arrays of objects of the specified types
	 */
	@Override
	public Stream<Object[]> extract(Cursor cursor)
	{
		return StreamSupport.stream(new CursorSpliterator<Object[]>()
		{

			@Override
			public boolean tryAdvance(Consumer<? super Object[]> action)
			{
				if (!cursor.next())
					return false;

				Object[] result = new Object[types.length];
				for (int i = 0; i < types.length; i++)
					result[i] = cursor.getCurrentValue(types[i]);
				action.accept(result);

				return true;
			}

		}, false);
	}

}
