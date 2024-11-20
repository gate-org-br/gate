package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Length;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.type.SHA256;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

public class SHA256Converter implements Converter
{

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "SHA256 Hash";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Length.Implementation(64));
		constraints.add(new Pattern.Implementation("^[A-Fa-f0-9]{64}$"));
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
				return SHA256.of(string);
		}
		return null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : SHA256.of(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : SHA256.of(value);
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
