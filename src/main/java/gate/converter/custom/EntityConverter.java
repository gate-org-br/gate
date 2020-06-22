package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.lang.property.Entity;
import gate.lang.property.Property;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class EntityConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string != null && string.trim().length() > 0)
			{
				Property property = Property.getProperty(type, Entity.getId(type));
				Object entity = type.getDeclaredConstructor().newInstance();
				property.setValue(entity, Converter
					.getConverter(property.getRawType())
					.ofString(property.getRawType(), string));
				return entity;
			}

			return null;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
			| InvocationTargetException | ConversionException | RuntimeException e)
		{
			throw new ConversionException(String.format("%s não é uma entidade válida.", string));
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		try
		{
			if (object == null)
				return "";
			return Converter.toString(Property.getProperty(type, Entity.getId(type))
				.getValue(object));
		} catch (RuntimeException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException,
		ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(Object.class, value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(Object.class, value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, toString(Object.class, value));
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
