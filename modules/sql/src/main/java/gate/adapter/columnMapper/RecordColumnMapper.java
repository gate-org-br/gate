package gate.adapter.columnMapper;

import gate.adapter.jsonConverter.JsonConverter;
import gate.lang.json.JsonElement;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class RecordColumnMapper implements ColumnMapper
{
	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Type type) throws SQLException
	{
		var json = rs.getString(fields);
		return rs.wasNull() ? null : JsonConverter.fromJson(type, JsonElement.parse(json));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Type type) throws SQLException
	{
		var json = rs.getString(fields);
		return rs.wasNull() ? null : JsonConverter.fromJson(type, JsonElement.parse(json));
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value)
			throws SQLException
	{
		if (value != null)
			ps.setString(fields++, JsonElement.encode(value).toString());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
