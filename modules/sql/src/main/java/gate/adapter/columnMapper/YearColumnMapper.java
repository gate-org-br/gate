package gate.adapter.columnMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Year;


public class YearColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		int year = rs.getInt(index);
		return rs.wasNull() ? null : Year.of(year);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		int year = rs.getInt(fields);
		return rs.wasNull() ? null : Year.of(year);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setInt(index++, ((Year) value).getValue());
		else
			ps.setNull(index++, Types.INTEGER);
		return index;
	}
}