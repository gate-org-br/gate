package gate.type;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

public class Tax implements Serializable
{

	@Serial private static final long serialVersionUID = 1L;

	private final BigDecimal value;

	private Tax(BigDecimal value) {this.value = value;}

	public static Tax valueOf(BigDecimal value)
	{
		return new Tax(value.setScale(6, RoundingMode.HALF_EVEN));
	}

	public static Tax valueOf(String string) throws ParseException
	{
		DecimalFormat format = new DecimalFormat("0.000000");
		format.setCurrency(Currency.getInstance(Locale.getDefault()));
		format.setParseBigDecimal(true);
		return new Tax((BigDecimal) format.parse(string));
	}

	@Override
	public String toString()
	{
		DecimalFormat format = new DecimalFormat("0.000000");
		format.setCurrency(Currency.getInstance(Locale.getDefault()));
		format.setParseBigDecimal(true);
		return format.format(value);
	}

	public BigDecimal getValue()
	{
		return value;
	}

	public Tax add(BigDecimal value)
	{
		return new Tax(this.value.add(value));
	}

	public Tax sub(BigDecimal value)
	{
		return new Tax(this.value.subtract(value));
	}

	public Tax mul(int value)
	{
		return new Tax(this.value.multiply(new BigDecimal(value)));
	}

	public Tax div(int value)
	{
		return new Tax(this.value.divide(new BigDecimal(value), RoundingMode.HALF_EVEN));
	}

	public Tax mul(BigDecimal value)
	{
		return new Tax(this.value.multiply(value));
	}

	public Tax div(BigDecimal value)
	{
		return new Tax(this.value.divide(value, RoundingMode.HALF_EVEN));
	}

	public BigDecimal get(BigDecimal value)
	{
		return this.value.multiply(value).divide(new BigDecimal(100), RoundingMode.HALF_EVEN);
	}
}