package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.converter.Converter;
import gate.type.Phone;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PhoneConverter implements Converter
{

	private static final Pattern XZEROZERO = Pattern.compile("^[1-9][0]{2}[0-9]{7}$");

	private static final Pattern ZEROXZEROZERO = Pattern.compile("^0[1-9][0]{2}[0-9]{7}$");

	private static final Pattern FIX = Pattern.compile("^[2-9][0-9]{7}$");
	private static final Pattern MOB = Pattern.compile("^9[0-9]{8}$");

	private static final Pattern DDD_FIX = Pattern.compile("^[1-9]{2}[2-9][0-9]{7}$");
	private static final Pattern DDD_MOB = Pattern.compile("^[1-9]{2}9[0-9]{8}$");

	private static final Pattern OP_DDD_FIX = Pattern.compile("^[1-9]{2}[1-9]{2}[2-9][0-9]{7}$");
	private static final Pattern OP_DDD_MOB = Pattern.compile("^[1-9]{2}[1-9]{2}9[0-9]{8}$");

	private static final Pattern ZERO_OP_DDD_FIX = Pattern.compile("^0[1-9]{2}[1-9]{2}[2-9][0-9]{7}$");
	private static final Pattern ZERO_OP_DDD_MOB = Pattern.compile("^0[1-9]{2}[1-9]{2}9[0-9]{8}$");

	@Override
	public String getDescription()
	{
		return "Campos de número de telefone devem ser compostos apenas por dígitos";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return new ArrayList<>();
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null || string.isEmpty())
				return null;
			return new Phone(string);
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(string.concat(" não é um número de telefone válido."));
		}
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		String value = ((Phone) object).getValue();

		if (XZEROZERO.matcher(value).matches())
			return String
					.format("(%c%c%c) %c%c%c%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value.charAt(5),
							value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9));

		if (ZEROXZEROZERO.matcher(value).matches())
			return String.format("(%c%c%c%c) %c%c%c%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value.charAt(
					5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10));

		if (FIX.matcher(value).matches())
			return String.format("%c%c%c%c-%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value.charAt(5),
					value.charAt(6), value.charAt(7));

		if (MOB.matcher(value).matches())
			return String.format("%c%c%c%c%c-%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value.charAt(5),
					value.charAt(6), value.charAt(7), value.charAt(8));

		if (DDD_FIX.matcher(value).matches())
			return String.format("(%c%c) %c%c%c%c-%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value
					.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9));

		if (DDD_MOB.matcher(value).matches())
			return String.format("(%c%c) %c%c%c%c%c-%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value
					.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10));

		if (OP_DDD_FIX.matcher(value).matches())
			return String.format("[%c%c] (%c%c) %c%c%c%c-%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value
					.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11));

		if (OP_DDD_MOB.matcher(value).matches())
			return String.format("[%c%c] (%c%c) %c%c%c%c%c-%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value
					.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11), value.charAt(12));

		if (ZERO_OP_DDD_FIX.matcher(value).matches())
			return String.format("[%c%c%c] (%c%c) %c%c%c%c-%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value
					.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11), value.charAt(12));

		if (ZERO_OP_DDD_MOB.matcher(value).matches())
			return String.format("[%c%c%c] (%c%c) %c%c%c%c%c-%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
					value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11), value.charAt(12),
					value.charAt(13));

		return value;
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
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : new Phone(value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : new Phone(value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value) throws SQLException
	{
		if (value != null)
			ps.setString(fields++, ((Phone) value).getValue());
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}
}
