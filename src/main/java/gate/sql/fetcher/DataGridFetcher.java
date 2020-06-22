package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.AppError;
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
			Class<?>[] types = cursor.getColumnTypes();

			while (cursor.next())
			{
				Object[] result = new Object[types.length];
				for (int i = 0; i < types.length; i++)
					result[i] = cursor.getCurrentValue(types[i]);
				dataSet.add(result);
			}

			return dataSet;
		} catch (SQLException e)
		{
			throw new AppError(e);
		}

	}
}
