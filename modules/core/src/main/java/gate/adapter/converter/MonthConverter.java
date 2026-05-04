package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;

import java.lang.reflect.Type;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;

@Description("Mês do Ano")
public class MonthConverter implements Converter
{
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
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		return Month.valueOf(string);
	}

}