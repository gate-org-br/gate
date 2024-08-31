package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.util.Captcha;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class CaptchaConverter implements Converter
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
	public Object ofString(Class<?> type, String string)
		throws ConversionException
	{
		if (string != null && string.trim().length() > 0)
			return Captcha.of(string);
		return null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object == null ? "" : object.toString();
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
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type)
		throws SQLException
	{
		try
		{
			String string = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return Captcha.of(string);
		} catch (ConversionException ex)
		{
			throw new SQLException(ex.getMessage(), ex);
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
		throws SQLException
	{
		try
		{
			String string = rs.getString(fields);
			if (rs.wasNull())
				return null;
			return Captcha.of(string);
		} catch (ConversionException ex)
		{
			throw new SQLException(ex.getMessage(), ex);
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
