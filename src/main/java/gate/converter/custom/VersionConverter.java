package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Version;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class VersionConverter implements Converter
{

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
			= Arrays.asList(new Pattern.Implementation(Version.PATTERN.toString()));

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Número de versão padrão maven";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
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
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
			{
				try
				{
					return Version.of(string);
				} catch (ParseException ex)
				{
					throw new ConversionException(string + " não é um número de versão válido.", ex);
				}
			}
		}
		return null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		String string = rs.getString(fields);
		try
		{
			return rs.wasNull() ? null : Version.of(string);
		} catch (ParseException ex)
		{
			throw new ConversionException(string + " não é um número de versão válido.", ex);
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String string = rs.getString(fields);
		try
		{
			return rs.wasNull() ? null : Version.of(string);
		} catch (ParseException ex)
		{
			throw new ConversionException(string + " não é um número de versão válido.", ex);
		}
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, value.toString());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
