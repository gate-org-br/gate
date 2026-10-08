package gate.adapter.converter;

import gate.adapter.metadata.Metadata;
import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.Range;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@Description("Campos de intervalo de datas devem ser preenchidos no formato MIN - MAX or NUM")
public class RangeConverter implements Converter
{
	private static final List<Constraint.Implementation<?>> CONSTRAINTS
			= Collections.singletonList(new Pattern.Implementation("^ *([0-9]+) *([-] *([0-9]+))? *$"));

	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public Range ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;

		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			return Range.valueOf(string);
		} catch (ParseException e)
		{
			throw new ConversionException(Metadata.getMetadata(Reflection.getRawType(type)).description());
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}
}