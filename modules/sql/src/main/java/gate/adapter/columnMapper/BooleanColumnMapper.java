package gate.adapter.columnMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class BooleanColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		boolean value = rs.getBoolean(index);
		return rs.wasNull() ? null : value;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		boolean value = rs.getBoolean(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setBoolean(index++, (Boolean) value);
		else
			ps.setNull(index++, Types.BOOLEAN);
		return index;
	}
}