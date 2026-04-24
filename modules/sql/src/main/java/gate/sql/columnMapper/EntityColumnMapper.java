package gate.sql.columnMapper;

import gate.annotation.Entity;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.lang.property.Property;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class EntityColumnMapper implements ColumnMapper
{

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(index);
		return rs.wasNull() ? null : ofString(type, value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(type, value);
	}

	private Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string != null && !string.trim().isEmpty())
			{
				Property property = Property.getProperty(type, Entity.Extractor.extract(type));
				Object entity = type.getDeclaredConstructor().newInstance();
				property.setValue(entity, Converter.getConverter(property.getRawType())
						.ofString(property.getRawType(), string));
				return entity;
			}
			return null;
		} catch (ReflectiveOperationException | RuntimeException e)
		{
			throw new ConversionException(String.format("%s não é uma entidade válida.", string));
		}
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(index++, Converter.toString(Property.getProperty(value.getClass(),
					Entity.Extractor.extract(value.getClass())).getValue(value)));
		else
			ps.setNull(index++, Types.VARCHAR);
		return index;
	}
}