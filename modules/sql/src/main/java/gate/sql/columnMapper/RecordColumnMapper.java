package gate.sql.columnMapper;

import gate.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class RecordColumnMapper implements ColumnMapper
{
	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		var json = rs.getString(fields);
		return rs.wasNull() ? null : Converter.fromJson(type, json);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		var json = rs.getString(fields);
		return rs.wasNull() ? null : Converter.fromJson(type, json);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value)
			throws SQLException
	{
		if (value != null)
			ps.setString(fields++, Converter.toJson(value));
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}