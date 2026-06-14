package gate.adapter.columnMapper;

import gate.adapter.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.lang.json.JsonElement;
import gate.lang.property.Property;
import gate.annotation.Entity;
import gate.type.DataFile;
import gate.type.Range;

import java.math.BigDecimal;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.*;
import java.util.Arrays;
import java.util.List;


public class LongColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Type type) throws SQLException
	{
		long value = rs.getLong(index);
		return rs.wasNull() ? null : value;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Type type) throws SQLException
	{
		long value = rs.getLong(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setLong(index++, (Long) value);
		else
			ps.setNull(index++, Types.BIGINT);
		return index;
	}
}
