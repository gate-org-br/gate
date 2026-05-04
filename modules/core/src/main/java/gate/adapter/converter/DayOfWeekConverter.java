package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;

import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.util.LinkedList;
import java.util.List;

@Description("Dia da Semana")
public class DayOfWeekConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Pattern.Implementation("^FRIDAY|MONDAY|SATURDAY|SUNDAY|THURSDAY|TUESDAY|WEDNESDAY$"));
		return constraints;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((DayOfWeek) object).name() : "";
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		return DayOfWeek.valueOf(string);
	}

}