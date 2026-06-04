package gate.adapter.columnMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;


public class DurationColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		long value = rs.getLong(index);
		return rs.wasNull() ? null : Duration.ofSeconds(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		long value = rs.getLong(fields);
		return rs.wasNull() ? null : Duration.ofSeconds(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setLong(index++, ((Duration) value).getSeconds());
		else
			ps.setNull(index++, Types.BIGINT);
		return index;
	}
}