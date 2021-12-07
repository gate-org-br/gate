package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class DateConverter implements Converter
{

	private static final java.util.regex.Pattern SIMPLE_PATTERN = java.util.regex.Pattern.compile("^[0-9]{8}$");
	private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}$");
	private static final java.util.regex.Pattern ISO_PATTERN = java.util.regex.Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
		= Arrays.asList(new Maxlength.Implementation(10),
			new Pattern.Implementation("^[0-9]{8}|[0-9]{2}[/][0-9]{2}[/][0-9]{4}$"));

	@Override
	public String getDescription()
	{
		return "Campos de data devem ser preenchidos no formato DD/MM/YYYY";
	}

	@Override
	public String getMask()
	{
		return "##/##/####";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			if (PATTERN.matcher(string).matches())
				return Date.of(string);
			else if (ISO_PATTERN.matcher(string).matches())
				return Date.formatter("yyyy-MM-dd").parse(string);
			else if (SIMPLE_PATTERN.matcher(string).matches())
				return Date.formatter("ddMMyyyy").parse(string);
			else
				throw new ParseException(string, 0);
		} catch (ParseException ex)
		{
			throw new ConversionException(ex, getDescription());
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
		return object != null ? Date.formatter(format).format((Date) object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toISOString(Class<?> type, Object object)
	{
		return object != null ? Date.formatter("yyyy-MM-dd").format((Date) object) : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		java.sql.Date value = rs.getDate(fields);
		return rs.wasNull() ? null : Date.of(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		java.sql.Date value = rs.getDate(fields);
		return rs.wasNull() ? null : Date.of(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setDate(fields++, new java.sql.Date(((Date) value).getValue()));
		else
			ps.setNull(fields++, Types.DATE);
		return fields;
	}
}
