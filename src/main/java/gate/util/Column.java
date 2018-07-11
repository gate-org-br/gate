package gate.util;

public class Column
{

	private final int min;
	private final int max;

	public Column(int min, int max)
	{
		this.min = min;
		this.max = max;
	}

	public String get(String string)
	{
		return string.substring(min, max).trim();
	}
}
