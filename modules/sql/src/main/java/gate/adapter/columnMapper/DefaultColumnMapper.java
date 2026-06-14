package gate.adapter.columnMapper;

import gate.error.ConversionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
	public Object readFromResultSet(ResultSet rs, int index, Type type)
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
			throw new ConversionException(ex.getMessage(), ex);
		} catch (InvocationTargetException ex)
		{
			Throwable cause = ex.getCause();
			if (cause instanceof ConversionException conversionException)
				throw conversionException;
			throw new ConversionException(cause.getMessage(), cause);
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Type type)
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
			throw new ConversionException(ex.getMessage(), ex);
		} catch (InvocationTargetException ex)
		{
			Throwable cause = ex.getCause();
			if (cause instanceof ConversionException conversionException)
				throw conversionException;
			throw new ConversionException(cause.getMessage(), cause);
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
