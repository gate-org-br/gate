package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BigDecimalConverter implements Converter
{

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
			= Arrays.asList(new Pattern.Implementation("^[0-9]+(,[0-9]{1,2})?$"));

	private static class Format
	{

		private static final DecimalFormat VALUE
				= (DecimalFormat) DecimalFormat
						.getInstance(Locale.getDefault());

		static
		{
			VALUE.setParseBigDecimal(true);
		}

	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

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
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null)
				return null;
			string = string.trim();
			if (string.isEmpty())
				return null;

			BigDecimal bd = (BigDecimal) Format.VALUE.parse(string);
			bd = bd.setScale(2, RoundingMode.UNNECESSARY);
			return bd;
		} catch (ParseException ex)
		{
			throw new ConversionException(String.format("%s não representa um número com duas casas decimais válido.",
					string));
		}

	}

	@Override
	public Number toNumber(Class<?> type, Object object)
	{
		return ((BigDecimal) object).setScale(2, RoundingMode.HALF_EVEN);
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? Format.VALUE.format(object) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? Format.VALUE.format(object) : "";
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException, ConversionException
	{
		BigDecimal value = rs.getBigDecimal(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		BigDecimal value = rs.getBigDecimal(fields);
		return rs.wasNull() ? null : value;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setBigDecimal(fields++, (BigDecimal) value);
		else
			ps.setNull(fields++, Types.DECIMAL);
		return fields;
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		switch (scanner.getCurrent().getType())
		{
			case NULL:
				scanner.scan();
				return null;
			case NUMBER:
				BigDecimal value = new BigDecimal(scanner.getCurrent().toString());
				scanner.scan();
				return value;
			default:
				throw new ConversionException(scanner.getCurrent() + " is not a number");
		}
	}

	@Override
	public <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		if (object == null)
			writer.write(JsonToken.Type.NULL, null);
		else if (object instanceof BigDecimal)
			writer.write(JsonToken.Type.NUMBER, object.toString());
		else
			throw new ConversionException(object.getClass().getName() + " is not a BigDecimal");
	}
}
