package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Length;
import gate.error.ConversionException;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.type.MD5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

public class MD5Converter implements Converter
{

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "MD5 Hash";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Length.Implementation(32));
		constraints.add(new Pattern.Implementation("^[A-Fa-f0-9]{32}$"));
		return constraints;
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
	public Object ofString(Class<?> type, String string)
	{
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
				return MD5.of(string);
		}
		return null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : MD5.of(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : MD5.of(value);
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
