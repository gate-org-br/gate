package gate.i18n;

import java.text.*;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

public final class CurrentLocale
{
	private static final ThreadLocal<Locale> CURRENT =
			ThreadLocal.withInitial(Locale::getDefault);

	private CurrentLocale() {}

	public static Locale get() {return CURRENT.get();}
	public static void set(Locale locale) {CURRENT.set(Objects.requireNonNull(locale));}
	public static void clear() {CURRENT.remove();}

	public static NumberFormat getIntegerFormat()
	{
		NumberFormat format = NumberFormat.getIntegerInstance(get());
		format.setGroupingUsed(true);
		return new StrictNumberFormat(format);
	}

	public static NumberFormat getDecimalFormat()
	{
		NumberFormat format = NumberFormat.getNumberInstance(get());
		format.setGroupingUsed(true);
		format.setMinimumFractionDigits(1);
		return new StrictNumberFormat(format);
	}

	public static DecimalFormat getDecimalFormat(String pattern)
	{
		return new DecimalFormat(
				pattern,
				DecimalFormatSymbols.getInstance(get()));
	}

	public static NumberFormat getBigDecimalFormat()
	{
		DecimalFormat format =
				(DecimalFormat) NumberFormat.getNumberInstance(get());

		format.setGroupingUsed(true);
		format.setParseBigDecimal(true);

		return new StrictNumberFormat(format);
	}

	public static Currency getCurrency() {return Currency.getInstance(get());}

	public static final class StrictNumberFormat extends NumberFormat
	{
		private final NumberFormat delegate;

		public StrictNumberFormat(NumberFormat delegate) {this.delegate = Objects.requireNonNull(delegate);}

		@Override
		public Number parse(String source)
				throws ParseException
		{
			ParsePosition position = new ParsePosition(0);

			Number number = parse(source, position);

			if (number == null)
				throw new ParseException(source, position.getErrorIndex());

			return number;
		}

		@Override
		public Number parse(String source, ParsePosition position)
		{
			int start = position.getIndex();

			Number number = delegate.parse(source, position);

			if (number == null || position.getIndex() != source.length())
			{
				position.setErrorIndex(position.getIndex());
				position.setIndex(start);
				return null;
			}

			return number;
		}

		@Override
		public StringBuffer format(
				double number,
				StringBuffer toAppendTo,
				FieldPosition pos)
		{
			return delegate.format(number, toAppendTo, pos);
		}

		@Override
		public StringBuffer format(
				long number,
				StringBuffer toAppendTo,
				FieldPosition pos)
		{
			return delegate.format(number, toAppendTo, pos);
		}

		@Override
		public AttributedCharacterIterator formatToCharacterIterator(Object obj) {return delegate.formatToCharacterIterator(obj);}
	}
}