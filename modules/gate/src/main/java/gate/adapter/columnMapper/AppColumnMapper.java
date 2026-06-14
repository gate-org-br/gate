package gate.adapter.columnMapper;


import gate.adapter.converter.Converter;
import gate.error.ConversionException;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class AppColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Type type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(index);
		return rs.wasNull() ? null : Converter.getConverter(type).ofString(type, value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Type type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : Converter.getConverter(type).ofString(type, value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value)
			throws SQLException
	{
		if (value != null)
			ps.setString(index++, Converter.getConverter(value.getClass()).toString(value.getClass(), value));
		else
			ps.setNull(index++, Types.VARCHAR);
		return index;
	}
}
