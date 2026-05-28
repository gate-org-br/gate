package gate.adapter.columnMapper;

import gate.error.ConversionException;
import gate.lang.json.JsonElement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ObjectColumnMapper implements ColumnMapper
{
	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(index);
		if (rs.wasNull())
			return null;
		return JsonElement.parse(value).decode(type);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		return JsonElement.parse(value).decode(type);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value)
			throws SQLException
	{
		if (value != null)
			ps.setString(index++, JsonElement.encode(value).toString());
		else
			ps.setNull(index++, Types.VARCHAR);
		return index;
	}
}