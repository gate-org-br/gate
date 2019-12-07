package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.DataConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Icon("2124")
@Converter(DataConverter.class)
public class Data extends Number implements Serializable, Comparable<Data>
{

	private static final long serialVersionUID = 1L;

	private final BigDecimal bytes;
	public static final Data ZERO = new Data(0);
	private final static BigDecimal FACTOR = new BigDecimal(1024);
	private final static Pattern VAL = Pattern.compile("^[0-9]+([,][0-9]{1,2})?[BKMGTPEZY]$");

	public Data()
	{
		this(BigDecimal.ZERO);
	}

	public Data(long bytes)
	{
		this(new BigDecimal(bytes));
	}

	public Data(BigDecimal bytes)
	{
		this.bytes = bytes.setScale(0, RoundingMode.DOWN);
	}

	public Data(BigDecimal value, Unit unit)
	{
		this.bytes = value.multiply(unit.getValue()).setScale(0, RoundingMode.DOWN);
	}

	public Data(long value, Unit unit)
	{
		this(new BigDecimal(value), unit);
	}

	public Data(String value)
	{
		try
		{
			value = value.replaceAll("\\s+", "").toUpperCase();
			if (!VAL.matcher(value).matches())
				throw new IllegalArgumentException("value");

			Unit unit = Unit.valueOf(value.substring(value.length() - 1));

			DecimalFormat df = new DecimalFormat();
			df.setGroupingUsed(false);
			df.setParseBigDecimal(true);
			df.setMaximumFractionDigits(2);
			df.setMinimumFractionDigits(0);
			this.bytes = ((BigDecimal) df.parse(value.substring(0, value.length() - 1))).multiply(unit.getValue()).
					setScale(0, RoundingMode.DOWN);
		} catch (ParseException e)
		{
			throw new IllegalArgumentException("value");
		}
	}

	@Override
	public String toString()
	{
		Unit unit = Unit.B;
		BigDecimal value = BigDecimal.ZERO;

		for (Unit u : Unit.values())
		{
			if (bytes.compareTo(u.getValue()) >= 0)
			{
				unit = u;
				value = bytes.divide(unit.getValue());
			}
		}

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);
		return df.format(value) + unit.name();
	}

	public BigDecimal getBytes()
	{
		return bytes;
	}

	public Data sub(Data o)
	{
		return new Data(bytes.compareTo(o.bytes) >= 0 ? bytes.subtract(o.bytes) : BigDecimal.ZERO);
	}

	public Data add(Data o)
	{
		return new Data(bytes.add(o.bytes));
	}

	@Override
	public int intValue()
	{
		return getBytes().intValue();
	}

	@Override
	public long longValue()
	{
		return getBytes().longValue();
	}

	@Override
	public float floatValue()
	{
		return getBytes().floatValue();
	}

	@Override
	public double doubleValue()
	{
		return getBytes().doubleValue();
	}

	public enum Unit
	{

		B(BigDecimal.ONE), K(BigDecimal.ONE.multiply(FACTOR)), M(BigDecimal.ONE.multiply(FACTOR).multiply(FACTOR)), G(
				BigDecimal.ONE.multiply(FACTOR).multiply(FACTOR).multiply(FACTOR)), T(BigDecimal.ONE.multiply(FACTOR).
				multiply(FACTOR).multiply(FACTOR).multiply(FACTOR)), P(BigDecimal.ONE.multiply(FACTOR).multiply(FACTOR).
				multiply(FACTOR).multiply(FACTOR).multiply(FACTOR)), E(BigDecimal.ONE.multiply(FACTOR).multiply(FACTOR).
				multiply(FACTOR).multiply(FACTOR).multiply(FACTOR).multiply(FACTOR)), Z(BigDecimal.ONE.multiply(FACTOR).
				multiply(FACTOR).multiply(FACTOR).multiply(FACTOR).multiply(FACTOR).multiply(FACTOR).multiply(FACTOR)), Y(
				BigDecimal.ONE.multiply(FACTOR).multiply(FACTOR).multiply(FACTOR).multiply(FACTOR).multiply(FACTOR).
						multiply(FACTOR).multiply(FACTOR).multiply(FACTOR));

		private final BigDecimal value;

		Unit(BigDecimal value)
		{
			this.value = value;
		}

		public BigDecimal getValue()
		{
			return value;
		}
	}

	public boolean lt(Data o)
	{
		return bytes.compareTo(o.bytes) < 0;
	}

	public boolean le(Data o)
	{
		return bytes.compareTo(o.bytes) <= 0;
	}

	public boolean eq(Data o)
	{
		return bytes.compareTo(o.bytes) == 0;
	}

	public boolean ge(Data o)
	{
		return bytes.compareTo(o.bytes) >= 0;
	}

	public boolean gt(Data o)
	{
		return bytes.compareTo(o.bytes) > 0;
	}

	@Override
	public int compareTo(Data o)
	{
		return bytes.compareTo(o.bytes);
	}

	public static Data sum(Stream<Data> values)
	{
		return new Data(values.filter(Objects::nonNull).map(Data::getBytes)
				.reduce(BigDecimal.ZERO, BigDecimal::add));
	}
}
