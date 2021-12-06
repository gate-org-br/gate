package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.PercentageConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

@Converter(PercentageConverter.class)
public class Percentage extends Number implements Serializable, Comparable<Percentage>
{

	private final BigDecimal value;
	private static final long serialVersionUID = 1L;

	public static final Percentage ZERO = new Percentage(BigDecimal.ZERO);
	public static final Percentage ONE_HUNDRED = new Percentage(new BigDecimal(100).setScale(2, RoundingMode.HALF_EVEN));

	public Percentage(BigDecimal value)
	{
		this.value = value.setScale(2, RoundingMode.HALF_EVEN);
	}

	public Percentage(String value) throws ParseException
	{
		DecimalFormat format = new DecimalFormat("0.00");
		format.setCurrency(Currency.getInstance(Locale.getDefault()));
		format.setParseBigDecimal(true);
		this.value = (BigDecimal) format.parse(value);
	}

	@Override
	public String toString()
	{
		DecimalFormat format = new DecimalFormat("0.00");
		format.setCurrency(Currency.getInstance(Locale.getDefault()));
		format.setParseBigDecimal(true);
		return format.format(value);
	}

	public BigDecimal getValue()
	{
		return value;
	}

	public Percentage add(BigDecimal value)
	{
		return new Percentage(this.value.add(value));
	}

	public Percentage sub(BigDecimal value)
	{
		return new Percentage(this.value.subtract(value));
	}

	public Percentage mul(int value)
	{
		return new Percentage(this.value.multiply(new BigDecimal(value)));
	}

	public Percentage div(int value)
	{
		return new Percentage(this.value.divide(new BigDecimal(value)));
	}

	public Percentage mul(BigDecimal value)
	{
		return new Percentage(this.value.multiply(value));
	}

	public Percentage div(BigDecimal value)
	{
		return new Percentage(this.value.divide(value));
	}

	public BigDecimal get(BigDecimal value)
	{
		return this.value.multiply(value).divide(new BigDecimal(100));
	}

	public BigDecimal getCoefficient()
	{
		if (value.compareTo(BigDecimal.ZERO) == 0)
			return value;
		return value.divide(new BigDecimal(100), 10, RoundingMode.HALF_EVEN);
	}

	@Override
	public int compareTo(Percentage o)
	{
		return value.compareTo(o.value);
	}

	@Override
	public int intValue()
	{
		return value.intValue();
	}

	@Override
	public long longValue()
	{
		return value.longValue();
	}

	@Override
	public float floatValue()
	{
		return value.floatValue();
	}

	@Override
	public double doubleValue()
	{
		return value.doubleValue();
	}
}
