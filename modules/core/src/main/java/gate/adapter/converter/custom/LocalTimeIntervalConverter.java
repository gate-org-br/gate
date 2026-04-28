package gate.adapter.converter.custom;

import gate.adapter.metadata.Metadata;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.adapter.converter.Converter;
import gate.error.ConversionException;
import gate.type.LocalTimeInterval;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de intervalo de hora devem ser preenchidos no formato HH:MM - HH:MM")
public class LocalTimeIntervalConverter implements Converter
{

	private static final List<String> SUFIXES = Arrays.asList("min", "max");

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(13));
		constraints.add(new Pattern.Implementation("^[0-9]{2}:[0-9]{2} - [0-9]{2}:[0-9]{2}$"));
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
			return LocalTimeInterval.of(string);
		} catch (ParseException ex)
		{
			throw new ConversionException(ex, Metadata.getMetadata(type).description());
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
		return object != null ? LocalTimeInterval.formatter(format).format((LocalTimeInterval) object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((LocalTimeInterval) object).toString() : "";
	}


}