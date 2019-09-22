package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.MoneyConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

@Icon("$")
@Converter(MoneyConverter.class)
public final class Money extends Number implements Comparable<Money>, Serializable
{

	private final BigDecimal value;
	private static final long serialVersionUID = 1L;

	public static final Money ZERO = new Money(BigDecimal.ZERO);

	public Money()
	{
		this.value = BigDecimal.ZERO;
	}

	public Money(long value)
	{
		this.value = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
	}

	public Money(BigDecimal value)
	{
		this.value = value.setScale(2, RoundingMode.HALF_EVEN);
	}

	public Money(String value) throws ParseException
	{
		this.value = (BigDecimal) getFormat().parse(value);
	}

	public Money(Locale locale, String value) throws ParseException
	{
		this.value = (BigDecimal) getFormat(locale).parse(value);
	}

	public Money add(Money money)
	{
		return new Money(value.add(money.value));
	}

	public Money add(Percentage percentage)
	{
		return new Money(value.add(percentage.get(value)));
	}

	public Percentage percentage(Money money)
	{
		return new Percentage(money.value.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : getValue().divide(
			money.getValue(), 4, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100), new MathContext(2,
			RoundingMode.HALF_EVEN)));
	}

	public Money add(Tax tax)
	{
		return new Money(value.add(tax.get(value)));
	}

	public Money sub(Money money)
	{
		return new Money(value.subtract(money.value));
	}

	public Money mul(long value)
	{
		return mul(new BigDecimal(value));
	}

	public Money div(long value)
	{
		return div(new BigDecimal(value));
	}

	public Money mul(BigDecimal value)
	{
		return new Money(this.value.multiply(value).setScale(2, RoundingMode.HALF_EVEN));
	}

	public Money div(BigDecimal value)
	{
		return new Money(this.value.divide(value, 2, RoundingMode.HALF_EVEN));
	}

	public Money abs()
	{
		return new Money(value.abs());
	}

	public Money neg()
	{
		return new Money(value.multiply(new BigDecimal(-1)));
	}

	public BigDecimal getValue()
	{
		return value;
	}

	public Money distribute(int n, BigDecimal i)
	{
		if (i.compareTo(BigDecimal.ZERO) == 0)
			return div(new BigDecimal(n));

		BigDecimal c = i.add(BigDecimal.ONE).pow(n);
		c = BigDecimal.ONE.divide(c, 10, RoundingMode.UP);
		c = BigDecimal.ONE.subtract(c);
		c = i.divide(c, 10, RoundingMode.UP);
		return new Money(value.multiply(c, new MathContext(10, RoundingMode.HALF_EVEN)));
	}

	public Money distribute(int n, Percentage i)
	{
		return distribute(n, i.getCoefficient());
	}

	@Override
	public String toString()
	{
		return getFormat().format(value);
	}

	public String toString(Locale locale)
	{
		return getFormat(locale).format(value);
	}

	private static DecimalFormat getFormat(Locale locale)
	{
		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(locale);
		format.setGroupingSize(3);
		format.setGroupingUsed(true);
		format.setParseBigDecimal(true);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		return format;
	}

	private static DecimalFormat getFormat()
	{
		return getFormat(Locale.getDefault());
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Money && value.equals(((Money) obj).value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	public boolean isZero()
	{
		return value.compareTo(BigDecimal.ZERO) == 0;
	}

	public boolean isNegative()
	{
		return value.compareTo(BigDecimal.ZERO) < 0;
	}

	public boolean isPositive()
	{
		return value.compareTo(BigDecimal.ZERO) > 0;
	}

	public boolean eq(Money money)
	{
		return value.compareTo(money.value) == 0;
	}

	public boolean lt(Money money)
	{
		return value.compareTo(money.value) < 0;
	}

	public boolean le(Money money)
	{
		return value.compareTo(money.value) <= 0;
	}

	public boolean gt(Money money)
	{
		return value.compareTo(money.value) > 0;
	}

	public boolean ge(Money money)
	{
		return value.compareTo(money.value) >= 0;
	}

	public boolean ne(Money money)
	{
		return value.compareTo(money.value) != 0;
	}

	@Override
	public int compareTo(Money o)
	{
		return value.compareTo(o.value);
	}

	@Override
	public double doubleValue()
	{
		return getValue().doubleValue();
	}

	@Override
	public float floatValue()
	{
		return getValue().floatValue();
	}

	@Override
	public int intValue()
	{
		return getValue().intValue();
	}

	@Override
	public long longValue()
	{
		return getValue().longValue();
	}

	public static Money sum(Stream<Money> values)
	{
		return new Money(values.filter(Objects::nonNull).map(Money::getValue).reduce(BigDecimal.ZERO, BigDecimal::add));
	}

	public static Money of(String string)
	{
		try
		{
			return new Money((BigDecimal) Money.getFormat().parse(string));
		} catch (ParseException ex)
		{
			throw new IllegalArgumentException(string + " is not a valid monetary value");
		}
	}

	public static Money of(long value)
	{
		return new Money(new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN));
	}

	public static Money of(BigDecimal value)
	{
		return new Money(value);
	}

	public static Money of(Locale locale, String string)
	{
		try
		{
			return new Money((BigDecimal) Money.getFormat(locale).parse(string));
		} catch (ParseException ex)
		{
			throw new IllegalArgumentException(string + " is not a valid monetary value");
		}
	}
}
