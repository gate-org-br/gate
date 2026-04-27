package gate.converter;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Description("Dia da Semana")
public class DayOfWeekConverter implements Converter
{

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? ((DayOfWeek) object).getDisplayName(TextStyle.FULL, Locale.getDefault()) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? ((DayOfWeek) object).getDisplayName(TextStyle.valueOf(format), Locale.getDefault()) : "";
	}

	@Override
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
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		return DayOfWeek.valueOf(string);
	}

}
