package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class YearMonthConverter implements Converter
{

	private static final DateTimeFormatter FORMATTTER = DateTimeFormatter.ofPattern("MM/yyyy");
	private static final List<Constraint.Implementation<?>> CONSTRAINTS = Arrays.asList(new Maxlength.Implementation(7), new Pattern.Implementation("^(0[123456789]|10|11|12)[/][0-9]{4}$"));

	@Override
	public String getMask()
	{
		return "##/####";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public String getDescription()
	{
		return "Campos de mês/ano devem ser preenchidos no formato MM/YYYY";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return FORMATTTER.format((YearMonth) object);
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return FORMATTTER.format((YearMonth) object);
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return DateTimeFormatter.ofPattern(format).format((YearMonth) object);
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
			return YearMonth.parse(string, FORMATTTER);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex, "%s não é uma mês/ano válido.%n%s.", ex.getParsedString(), getDescription());
		}
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int index,
		Class<?> type) throws SQLException
	{
		LocalDate localDate = rs.getObject(index, LocalDate.class);
		return localDate != null ? YearMonth.of(localDate.getYear(), localDate.getMonth()) : null;
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type) throws SQLException
	{
		LocalDate localDate = rs.getObject(fields, LocalDate.class);
		return localDate != null ? YearMonth.of(localDate.getYear(), localDate.getMonth()) : null;
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int index, Object value) throws SQLException
	{
		ps.setObject(index, ((YearMonth) value).atDay(1));
		return index + 1;
	}

}
