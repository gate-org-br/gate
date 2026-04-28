package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Description("Mês do Ano")
public class MonthConverter implements Converter
{

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? ((Month) object).getDisplayName(TextStyle.FULL, Locale.getDefault()) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? ((Month) object).getDisplayName(TextStyle.valueOf(format), Locale.getDefault()) : "";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Pattern.Implementation(
				"^APRIL|AUGUST|DECEMBER|FEBRUARY|JANUARY|JULY|JUNE|MARCH|MAY|NOVEMBER|OCTOBER|SEPTEMBER$"));
		return constraints;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Month) object).name() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		return Month.valueOf(string);
	}

}