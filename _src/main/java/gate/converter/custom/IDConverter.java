package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.ID;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

public class IDConverter implements Converter
{

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null)
				return null;

			string = string.trim();
			if (string.isEmpty())
				return null;

			return new ID(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(ex, string + " não é um número válido");
		}
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Campos de ID devem ser preenchidos com números inteiros positivos.";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(10));
		constraints.add(new Pattern.Implementation("^[0-9]{1,10}$"));
		return constraints;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : new ID(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		int value = rs.getInt(fields);
		return rs.wasNull() ? null : new ID(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setInt(fields++, ((ID) value).getValue());
		else
			ps.setNull(fields++, Types.INTEGER);
		return fields;
	}
}
