package gate.converter;

import gate.annotation.Name;
import gate.constraint.Constraint;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class EnumConverter implements Converter
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
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		if (Character.isDigit(string.charAt(0)))
			return type.getEnumConstants()[Integer.parseInt(string)];

		for (Object obj : type.getEnumConstants())
			if (((Enum<?>) obj).name().equals(string))
				return obj;
		throw new ConversionException("Invalid enum constant: " + string);
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? String.valueOf(((Enum<?>) object).ordinal()) : "";
	}

	@Override
	public String toText(Class<?> type, Object object)
	{

		return object != null
			? Name.Extractor.extract(object)
				.orElse(object.toString()) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, toText(type, object)) : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : type.getEnumConstants()[value];
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : type.getEnumConstants()[value];
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setInt(fields++, ((Enum<?>) value).ordinal());
		else
			ps.setNull(fields++, Types.INTEGER);
		return fields;
	}
}
