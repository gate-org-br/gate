package gate.type;

import gate.annotation.Icon;
import gate.annotation.Name;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

@Icon("2017")
public final class DurationInterval implements Serializable, Interval<Duration>
{

	private final Duration min;
	private final Duration max;

	private DurationInterval(Duration min, Duration max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.compareTo(max) > 0)
			throw new IllegalArgumentException("min must be <= max");

		this.min = min;
		this.max = max;
	}

	@Override
	public Duration getMin()
	{
		return min;
	}

	@Override
	public Duration getMax()
	{
		return max;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof DurationInterval && ((DurationInterval) obj).min.equals(min) && ((DurationInterval) obj).min.equals(min));
	}

	@Override
	public int hashCode()
	{
		return (int) (min.hashCode() + max.hashCode());
	}

	@Override
	public String toString()
	{
		return gate.converter.Converter.toString(min.toString())
			+ " - " + gate.converter.Converter.toString(min.toString());
	}

	public static DurationInterval of(Duration min, Duration max)
	{
		return new DurationInterval(min, max);
	}

	public static class Mutable
	{

		@Name("Duração mínima")
		private Duration min;

		@Name("Duração máxima")
		private Duration max;

		public Mutable()
		{

		}

		public Mutable(Duration min, Duration max)
		{
			this.min = min;
			this.max = max;
		}

		public Duration getMin()
		{
			return min;
		}

		public void setMin(Duration min)
		{
			this.min = min;
		}

		public Duration getMax()
		{
			return max;
		}

		public void setMax(Duration max)
		{
			this.max = max;
		}

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof Mutable
				&& Objects.equals(min, ((Mutable) obj).min)
				&& Objects.equals(max, ((Mutable) obj).max);
		}

		@Override
		public int hashCode()
		{
			return (min != null ? min.hashCode() : 0)
				+ (max != null ? max.hashCode() : 0);
		}
	}
}
