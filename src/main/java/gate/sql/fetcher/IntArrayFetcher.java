package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.Optional;

/**
 * Fetches the first row of a Cursor as a int array.
 *
 * @author Davi Nunes da Silva
 */
public class IntArrayFetcher implements Fetcher<Optional<int[]>>
{

	/**
	 * Fetches the first row of a Cursor as a int array.
	 *
	 * @param cursor the Cursor to be fetched
	 *
	 * @return an Optional describing the first row of the Cursor as a int array, or an empty Optional if the cursor is empty
	 */
	@Override
	public Optional<int[]> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();

		int[] result = new int[cursor.getColumnCount()];

		for (int i = 0; i < result.length; i++)
			result[i] = cursor.getIntValue(i + 1);
		return Optional.of(result);
	}
}
