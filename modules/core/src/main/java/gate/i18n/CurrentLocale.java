package gate.i18n;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.Locale;

public final class CurrentLocale
{
	private static final ThreadLocal<Locale> CURRENT = new ThreadLocal<>();

	public static Locale get()
	{
		Locale locale = CURRENT.get();
		return locale != null
				? locale : Locale.getDefault();
	}

	public static NumberFormat getIntegerFormat()
	{
		NumberFormat format = NumberFormat.getIntegerInstance(get());
		format.setGroupingUsed(true);
		return format;
	}

	public static NumberFormat getDecimalFormat()
	{
		NumberFormat format = NumberFormat.getNumberInstance(get());
		format.setGroupingUsed(true);
		format.setMinimumFractionDigits(1);
		return format;
	}

	public static DecimalFormat getDecimalFormat(String pattern)
	{
		return new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(get()));
	}

	public static DecimalFormat getBigDecimalFormat()
	{
		DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(get());
		format.setGroupingUsed(true);
		format.setParseBigDecimal(true);
		return format;
	}

	public static Number parseInteger(String string)
	{
		return parse(getIntegerFormat(), string);
	}

	public static Number parseDecimal(String string)
	{
		return parse(getDecimalFormat(), string);
	}

	public static Number parseBigDecimal(String string)
	{
		return parse(getBigDecimalFormat(), string);
	}

	private static Number parse(NumberFormat format, String string)
	{
		ParsePosition position = new ParsePosition(0);
		Number number = format.parse(string, position);

		if (number == null || position.getIndex() != string.length())
			throw new NumberFormatException(string);

		return number;
	}

	public static Currency getCurrency()
	{
		return Currency.getInstance(get());
	}

	public static void set(Locale locale)
	{
		CURRENT.set(locale);
	}

	public static void clear()
	{
		CURRENT.remove();
	}
}
