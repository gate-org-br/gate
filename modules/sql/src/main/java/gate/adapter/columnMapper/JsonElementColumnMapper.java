package gate.adapter.columnMapper;

import gate.lang.json.JsonElement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class JsonElementColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		String value = rs.getString(index);
		return rs.wasNull() ? null : JsonElement.parse(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : JsonElement.parse(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(index++, JsonElement.stringify((JsonElement) value));
		else
			ps.setNull(index++, Types.VARCHAR);
		return index;
	}
}