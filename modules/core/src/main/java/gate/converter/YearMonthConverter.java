package gate.converter;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Description("Campos de mês/ano devem ser preenchidos no formato MM/YYYY")
public class YearMonthConverter implements Converter
{

	private static final DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern("MM/yyyy");
	private static final List<Constraint.Implementation<?>> CONSTRAINTS = Arrays.asList(new Maxlength.Implementation(7),
			new Pattern.Implementation("^(0[123456789]|10|11|12)[/][0-9]{4}$"));

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? FORMATER.format((YearMonth) object) : "";
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? FORMATER.format((YearMonth) object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? DateTimeFormatter.ofPattern(format).format((YearMonth) object) : "";
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
			return YearMonth.parse(string, FORMATER);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex, "%s não é uma mês/ano válido.%n%s.", ex.getParsedString(), gate.lang.property.metadata.Metadata.getMetadata(type).description());
		}
	}

}