package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.type.Month;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class MonthConverter implements Converter
{

	@Override
	public String getDescription()
	{
		return "Campos de MÊS devem ser preenchidos no formato MM/YYYY. Os caracteres de formatação são opcionais";
	}

	@Override
	public String getMask()
	{
		return "##/####";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(7));
		constraints.add(new Pattern.Implementation("^__[/]____|[0-9]{6}|[0-9]{2}[/][0-9]{4}$"));
		return constraints;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		if (string.equals("__/____"))
			return null;
		try
		{
			return new Month(string);
		} catch (ParseException e)
		{
			throw new ConversionException(String.format(getDescription()));
		}
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? ((Month) object).format(format) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		java.sql.Date value = rs.getDate(fields);
		return rs.wasNull() ? null : new Month(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		java.sql.Date value = rs.getDate(fields);
		return rs.wasNull() ? null : new Month(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setDate(fields++, new java.sql.Date(((Month) value).getValue()));
		else
			ps.setNull(fields++, Types.DATE);
		return fields;
	}
}
