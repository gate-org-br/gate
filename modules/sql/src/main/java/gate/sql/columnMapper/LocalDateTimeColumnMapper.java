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


public class LocalDateTimeColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException
	{
		return rs.getObject(index, LocalDateTime.class);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		return rs.getObject(fields, LocalDateTime.class);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		ps.setObject(index, value);
		return index + 1;
	}
}
