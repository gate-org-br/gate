package gate.sql.columnMapper;

import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.lang.json.JsonElement;
import gate.lang.property.Property;
import gate.annotation.Entity;
import gate.type.DataFile;
import gate.type.Range;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.*;
import java.util.Arrays;
import java.util.List;


public class ClassColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		try
		{
			String value = rs.getString(index);
			return rs.wasNull() ? null : Thread.currentThread().getContextClassLoader().loadClass(value);
		} catch (ClassNotFoundException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		try
		{
			String value = rs.getString(fields);
			return rs.wasNull() ? null : Thread.currentThread().getContextClassLoader().loadClass(value);
		} catch (ClassNotFoundException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(index++, ((Class<?>) value).getName());
		else
			ps.setNull(index++, Types.VARCHAR);
		return index;
	}
}
