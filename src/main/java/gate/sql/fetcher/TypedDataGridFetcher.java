package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.AppError;
import gate.type.DataGrid;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TypedDataGridFetcher implements Fetcher<DataGrid>
{

	private final Class[] types;

	public TypedDataGridFetcher(Class[] types)
	{
		this.types = types;
	}

	@Override
	public DataGrid fetch(Cursor rs)
	{
		try
		{
			ResultSetMetaData rsmd = rs.getResultSet().getMetaData();
			String[] head = new String[rsmd.getColumnCount()];
			for (int i = 1; i <= rsmd.getColumnCount(); i++)
				head[i - 1] = rsmd.getColumnLabel(i);

			DataGrid dataSet = new DataGrid(head);

			while (rs.next())
			{
				Object[] result = new Object[types.length];
				for (int i = 0; i < types.length; i++)
					result[i] = rs.getValue(types[i], i + 1);
				dataSet.add(result);
			}

			return dataSet;
		} catch (SQLException e)
		{
			throw new AppError(e);
		}

	}
}
