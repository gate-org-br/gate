package gate.sql.fetcher;

import gate.error.InternalServerException;
import gate.sql.Cursor;
import gate.type.DataGrid;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DataGridFetcher implements Fetcher<DataGrid>
{

	public DataGridFetcher()
	{
	}

	@Override
	public DataGrid fetch(Cursor cursor)
	{
		try
		{
			ResultSetMetaData rsmd = cursor.getResultSet().getMetaData();
			String[] head = new String[rsmd.getColumnCount()];
			for (int i = 1; i <= rsmd.getColumnCount(); i++)
				head[i - 1] = rsmd.getColumnLabel(i);

			DataGrid dataSet = new DataGrid(head);

			while (cursor.next())
				dataSet.add(cursor.getColumnValues().toArray());

			return dataSet;
		} catch (SQLException ex)
		{
			throw new InternalServerException(ex.getMessage());
		}

	}
}