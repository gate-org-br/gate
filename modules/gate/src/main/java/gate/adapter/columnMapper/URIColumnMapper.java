package gate.adapter.columnMapper;


import gate.error.ConversionException;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class URIColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(index);
		return rs.wasNull() ? null : URI.create(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : URI.create(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value)
			throws SQLException
	{
		if (value != null)
			ps.setString(index++, value.toString());
		else
			ps.setNull(index++, Types.VARCHAR);
		return index;
	}
}