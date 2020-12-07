package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Range;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RangeConverter implements Converter
{

	private static final List<String> SUFIXES
		= Arrays.asList("min", "max");
	private static final List<Constraint.Implementation<?>> CONSTRAINTS
		= Collections.singletonList(new Pattern.Implementation("^ *([0-9]+) *([-] *([0-9]+))? *$"));

	@Override
	public String getDescription()
	{
		return "Campos de intervalo de datas devem ser preenchidos no formato MIN - MAX or NUM";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public Range ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;

		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			return Range.of(string);
		} catch (ParseException e)
		{
			throw new ConversionException(getDescription());
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
		return object != null ? object.toString() : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public List<String> getSufixes()
	{
		return SUFIXES;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		long min = rs.getLong(fields);
		if (rs.wasNull())
			return null;
		long max = rs.getLong(fields + 1);
		if (rs.wasNull())
			return null;
		return Range.of(min, max);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		long min = rs.getLong(fields + Converter.SEPARATOR + SUFIXES.get(0));
		if (rs.wasNull())
			return null;
		long max = rs.getLong(fields + Converter.SEPARATOR + SUFIXES.get(1));
		if (rs.wasNull())
			return null;
		return Range.of(min, max);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
		{
			ps.setLong(fields++, ((Range) value).getMin());
			ps.setLong(fields++, ((Range) value).getMax());
		} else
		{
			ps.setNull(fields++, Types.INTEGER);
			ps.setNull(fields++, Types.INTEGER);
		}
		return fields;
	}
}
