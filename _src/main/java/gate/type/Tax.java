package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.TaxConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

@Converter(TaxConverter.class)
public class Tax implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final BigDecimal value;

	public Tax(BigDecimal value)
	{
		this.value = value.setScale(6, RoundingMode.UNNECESSARY);
	}

	public Tax(String value) throws ParseException
	{
		DecimalFormat format = new DecimalFormat("0.000000");
		format.setCurrency(Currency.getInstance(Locale.getDefault()));
		format.setParseBigDecimal(true);
		this.value = (BigDecimal) format.parse(value);
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
		return new Tax(this.value.divide(new BigDecimal(value)));
	}

	public Tax mul(BigDecimal value)
	{
		return new Tax(this.value.multiply(value));
	}

	public Tax div(BigDecimal value)
	{
		return new Tax(this.value.divide(value));
	}

	public BigDecimal get(BigDecimal value)
	{
		return this.value.multiply(value).divide(new BigDecimal(100));
	}
}
