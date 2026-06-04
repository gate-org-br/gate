package gate.adapter.columnMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class ClassColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		try
		{
			String value = rs.getString(index);
			return rs.wasNull() ? null : Thread.currentThread().getContextClassLoader().loadClass(value);
		} catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		try
		{
			String value = rs.getString(fields);
			return rs.wasNull() ? null : Thread.currentThread().getContextClassLoader().loadClass(value);
		} catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(index++, ((Class<?>) value).getName());
		else
			ps.setNull(index++, Types.VARCHAR);
		return index;
	}
}