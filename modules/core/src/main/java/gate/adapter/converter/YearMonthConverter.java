package gate.adapter.converter;

import gate.adapter.metadata.Metadata;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Description("Campos de mês/ano devem ser preenchidos no formato MM/YYYY")
public class YearMonthConverter implements Converter
{

	private static final DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern("MM/yyyy");
	private static final DateTimeFormatter ISO_FORMATER = DateTimeFormatter.ofPattern("yyyy-MM");
	private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^(0[1-9]|10|11|12)/[0-9]{4}$");
	private static final java.util.regex.Pattern ISO_PATTERN = java.util.regex.Pattern.compile("^[0-9]{4}-(0[1-9]|10|11|12)$");
	private static final List<Constraint.Implementation<?>> CONSTRAINTS = Arrays.asList(new Maxlength.Implementation(7),
			new Pattern.Implementation("^(?:(?:0[1-9]|10|11|12)/[0-9]{4}|[0-9]{4}-(?:0[1-9]|10|11|12))$"));

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
	public String toISOString(Class<?> type, Object object)
	{
		return object != null ? ISO_FORMATER.format((YearMonth) object) : "";
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;

		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			if (PATTERN.matcher(string).matches())
				return YearMonth.parse(string, FORMATER);
			else if (ISO_PATTERN.matcher(string).matches())
				return YearMonth.parse(string, ISO_FORMATER);
			else
				throw new DateTimeParseException("Invalid year month format", string, 0);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException("%s não é uma mês/ano válido.%n%s."
					.formatted(ex.getParsedString(), Metadata.getMetadata(Reflection.getRawType(type)).description()), ex);
		}
	}

}