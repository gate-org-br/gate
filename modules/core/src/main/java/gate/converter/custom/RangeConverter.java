package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Range;
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
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}


}
