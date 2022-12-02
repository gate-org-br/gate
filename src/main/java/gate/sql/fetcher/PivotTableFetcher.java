package gate.sql.fetcher;

import gate.error.AppError;
import gate.sql.Cursor;
import gate.type.PivotTable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

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
		try
		{
			ResultSetMetaData rsmd = cursor.getResultSet().getMetaData();
			if (rsmd.getColumnCount() < 3)
				throw new java.lang.IllegalArgumentException("Attempt to fetch a pivot table from a cursor with less than 3 columns");

			PivotTable result = new PivotTable(rsmd.getColumnLabel(1), rsmd.getColumnLabel(2), rsmd.getColumnLabel(3));
			while (cursor.next())
				result.add(cursor.getValue(String.class, 1),
					cursor.getValue(String.class, 2),
					cursor.getValue(type, 3));
			return result;
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}
}
