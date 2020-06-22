package gate.type;

import java.io.Serializable;

public abstract class TimeAmount extends Number
	implements Serializable, Comparable<TimeAmount>
{

	private static final long serialVersionUID = 1L;

	protected final long value;
	protected final static long S = 1;
	protected final static long M = 60 * S;
	protected final static long H = 60 * M;
	protected final static long D = 24 * H;

	protected TimeAmount(long value)
	{
		this.value = value;
	}

	protected TimeAmount(long h, long m, long s)
	{
		this.value = (h * H) + (m * M) + (s * S);
	}

	public long getH()
	{
		return value / H;
	}

	public long getM()
	{
		return (value % H) / M;
	}

	public long getS()
	{
		return ((value % H) % M) / S;
	}

	public int asS()
	{
		return (int) Math.floor(value / S);
	}

	public int asM()
	{
		return (int) Math.floor(value / M);
	}

	public int asH()
	{
		return (int) Math.floor(value / H);
	}

	public int asD()
	{
		return (int) Math.floor(value / D);
	}

	public long getValue()
	{
		return value;
	}

	public boolean lt(TimeAmount o)
	{
		return value < o.value;
	}

	public boolean le(TimeAmount o)
	{
		return value <= o.value;
	}

	public boolean eq(TimeAmount o)
	{
		return value == o.value;
	}

	public boolean ge(TimeAmount o)
	{
		return value >= o.value;
	}

	public boolean gt(TimeAmount o)
	{
		return value > o.value;
	}

	@Override
	public int intValue()
	{
		return (int) getValue();
	}

	@Override
	public long longValue()
	{
		return getValue();
	}

	@Override
	public float floatValue()
	{
		return getValue();
	}

	@Override
	public double doubleValue()
	{
		return getValue();
	}

	@Override
	public int compareTo(TimeAmount o)
	{
		return Long.compare(value, o.value);
	}

	public abstract TimeAmount sub(TimeAmount value);

	public abstract TimeAmount add(TimeAmount value);

	public abstract TimeAmount divide(Integer value);

	public abstract TimeAmount multiply(Integer value);
}
