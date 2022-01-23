package gate.converter;

import gate.constraint.Constraint;
import gate.error.AppError;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class ClassConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getDescription()
	{
		return null;
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string)
	{
		try
		{
			return string != null && string.trim().length() > 0 ? Thread.currentThread().getContextClassLoader().loadClass(string) : null;
		} catch (ClassNotFoundException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Class<?>) object).getName() : "";
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? ((Class<?>) object).getName() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, toText(type, object)) : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
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
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, ((Class<?>) value).getName());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
