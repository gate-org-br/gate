package mock;

import java.util.Objects;

public final class EmailMock
{
	private final String value;

	public EmailMock(String value)
	{
		this.value = Objects.requireNonNull(value, "value is null").trim().toLowerCase();
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof EmailMock email && Objects.equals(value, email.value);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(value);
	}
}
