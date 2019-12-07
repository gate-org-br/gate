package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.type.EMail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

public class EMailConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Pattern.Implementation(EMail.REGEX));
		return constraints;
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Campos de EMAILS devem conter endereços de E-Mail válidos";
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
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			return string != null && string.trim().length() > 0 ? new EMail(string) : null;
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(string.concat(" não é um EMail válido."));
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : new EMail(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : new EMail(value);
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
