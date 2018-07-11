package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.type.Month;
import gate.type.MonthInterval;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MonthIntervalConverter implements Converter
{

	private static final List<String> SUFIXES
			= Arrays.asList("month1", "month2");

	@Override
	public String getDescription()
	{
		return "Campos de intervalo de meses devem ser preenchidos no formato MM/YYYY - MM/YYYY. Os caracteres de formatação são opcionais.";
	}

	@Override
	public String getMask()
	{
		return "##/#### - ##/####";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(17));
		constraints.add(new Pattern.Implementation("^(([0-9]{12})|([0-9]{2}[/][0-9]{4}[ ][-][ ][0-9]{2}[/][0-9]{4}))$"));
		return constraints;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string != null && string.trim().length() > 0)
		{
			try
			{
				String[] strings = string.contains(" - ") ? string.split(" - ") : new String[]
				{
					string.substring(0, string.length() / 2), string.substring(string.length() / 2, string.length())
				};

				if (string.matches("^[0-9]{12}$"))
					return new MonthInterval("MMyyyy", strings[0], strings[1]);
				else if (string.matches("^[0-9]{2}[/][0-9]{4}[ ][-][ ][0-9]{2}[/][0-9]{4}$"))
					return new MonthInterval("MM/yyyy", strings[0], strings[1]);
				else
					throw new ConversionException(String.format(getDescription()));
			} catch (ConversionException | ParseException e)
			{
				throw new ConversionException(String.format(getDescription()));
			}
		}

		return null;
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? ((MonthInterval) object).format(format) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((MonthInterval) object).format("MM/yyyy") : "";
	}

	@Override
	public List<String> getSufixes()
	{
		return SUFIXES;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		java.sql.Date value1 = rs.getDate(fields);
		if (rs.wasNull())
			return null;
		java.sql.Date value2 = rs.getDate(fields + 1);
		if (rs.wasNull())
			return null;
		return new MonthInterval(new Month(value1), new Month(value2));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		java.sql.Date value1 = rs.getDate(fields + ":" + SUFIXES.get(0));
		if (rs.wasNull())
			return null;
		java.sql.Date value2 = rs.getDate(fields + ":" + SUFIXES.get(1));
		if (rs.wasNull())
			return null;
		return new MonthInterval(new Month(value1), new Month(value2));
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setDate(fields++, new java.sql.Date(((MonthInterval) value).getMonth1().getValue()));
			ps.setDate(fields++, new java.sql.Date(((MonthInterval) value).getMonth2().getValue()));
		} else
		{
			ps.setNull(fields++, Types.DATE);
			ps.setNull(fields++, Types.DATE);
		}
		return fields;
	}
}
