package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.type.PivotTable;

/**
 * Fetches a Cursor as a PivotTable of the specified type.
 *
 * @param <T> the type of the PivotTable to be fetched
 */
public class PivotTableFetcher<T> implements Fetcher<PivotTable<T>>
{

	private final Class<T> type;

	/**
	 * Creates a new PivotFetcher with the specified type.
	 *
	 * @param type type of the java object to be fetched
	 */
	public PivotTableFetcher(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Fetches result as a PivotTable of the specified type.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return the result as a PivotTable of the specified type
	 */
	@Override
	public PivotTable<T> fetch(Cursor cursor)
	{
		PivotTable result = new PivotTable();
		while (cursor.next())
			result.add(cursor.getValue(String.class, 1),
				cursor.getValue(String.class, 2),
				cursor.getValue(type, 3));
		return result;
	}
}
