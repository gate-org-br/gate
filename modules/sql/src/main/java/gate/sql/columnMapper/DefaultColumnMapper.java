package gate.sql.columnMapper;

import gate.error.ConversionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class DefaultColumnMapper implements ColumnMapper
{
	private final Method factoryMethod;

	public DefaultColumnMapper(Method factoryMethod)
	{
		this.factoryMethod = factoryMethod;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(index);
		if (rs.wasNull())
			return null;
		value = value.trim();
		if (value.isEmpty())
			return null;

		try
		{
			return factoryMethod.invoke(null, value);
		} catch (IllegalAccessException ex)
		{
			throw new ConversionException(ex, ex.getMessage());
		} catch (InvocationTargetException ex)
		{
			Throwable cause = ex.getCause();
			if (cause instanceof ConversionException conversionException)
				throw conversionException;
			throw new ConversionException(cause, cause.getMessage());
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		if (rs.wasNull())
			return null;
		value = value.trim();
		if (value.isEmpty())
			return null;

		try
		{
			return factoryMethod.invoke(null, value);
		} catch (IllegalAccessException ex)
		{
			throw new ConversionException(ex, ex.getMessage());
		} catch (InvocationTargetException ex)
		{
			Throwable cause = ex.getCause();
			if (cause instanceof ConversionException conversionException)
				throw conversionException;
			throw new ConversionException(cause, cause.getMessage());
		}
	}


	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value)
			throws SQLException
	{
		if (value != null)
			ps.setString(index++, value.toString());
		else
			ps.setNull(index++, Types.VARCHAR);
		return index;
	}
}