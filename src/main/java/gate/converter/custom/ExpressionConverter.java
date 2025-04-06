package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.converter.Converter;
import gate.lang.expression.Expression;
import gate.lang.json.JsonScanner;
import static gate.lang.json.JsonToken.Type.NULL;
import static gate.lang.json.JsonToken.Type.STRING;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class ExpressionConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

	@Override
	public String getDescription()
	{
		return "Valid expression";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null || string.trim().isEmpty())
				return null;
			return Expression.of(string);
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(String.format("%s is not a valid expression.", string));
		}

	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return object.toString();
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return object.toString();
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		if (object == null)
			return "";
		return String.format(format, object);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : Expression.of(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : Expression.of(value);
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
