package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.converter.Converter;
import gate.lang.json.JsonElement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class JsonElementConverter implements Converter
{

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
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
				return JsonElement.parse(string);
		}
		return null;
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		if (object != null)
			return JsonElement.format((JsonElement) object);
		return null;
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		if (object != null)
			return String.format(format,
				JsonElement.format((JsonElement) object));
		return null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object != null)
			return JsonElement.format((JsonElement) object);
		return null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		if (!rs.wasNull())
			return JsonElement.parse(value);
		return null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		if (!rs.wasNull())
			return JsonElement.parse(value);
		return null;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, JsonElement.format((JsonElement) value));
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
