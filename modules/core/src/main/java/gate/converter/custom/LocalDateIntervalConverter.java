package gate.converter.custom;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.LocalDateInterval;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de intervalo de datas devem ser preenchidos no formato DD/MM/YYYY - DD/MM/YYYY")
public class LocalDateIntervalConverter implements Converter
{

	private static final List<String> SUFIXES = Arrays.asList("min", "max");

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(23));
		constraints.add(new Pattern.Implementation("^[0-9]{2}/[0-9]{2}/[0-9]{4} - [0-9]{2}/[0-9]{2}/[0-9]{4}$"));
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

		try
		{
			return LocalDateInterval.of(string);
		} catch (ParseException ex)
		{
			throw new ConversionException(ex, gate.lang.property.metadata.Metadata.getMetadata(type).description());
		}
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? LocalDateInterval.formatter(format).format((LocalDateInterval) object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}


}
