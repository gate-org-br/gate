package gate.type;

public class Color
{

	public static final Color BLACK = Color.of("#000000");
	public static final Color WHITE = Color.of("#FFFFFF");
	public static final Color RED = Color.of("#660000");
	public static final Color GREEN = Color.of("#006600");
	public static final Color BLUE = Color.of("#000066");
	public static final Color LIGHT_GRAY = Color.of("#CCCCCC");

	private final int r;
	private final int g;
	private final int b;

	private Color(int r, int g, int b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public int getR()
	{
		return r;
	}

	public int getG()
	{
		return g;
	}

	public int getB()
	{
		return b;
	}

	public static Color of(int r, int b, int g)
	{
		return new Color(r, g, b);
	}

	public static Color of(String string)
	{
		int r = Integer.parseInt(string.substring(1, 3), 16);
		int g = Integer.parseInt(string.substring(3, 5), 16);
		int b = Integer.parseInt(string.substring(5, 7), 16);
		return of(r, g, b);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Color && r == ((Color) obj).r
			&& g == ((Color) obj).g
			&& b == ((Color) obj).b;
	}

	@Override
	public int hashCode()
	{
		return r + g + b;
	}

	@Override
	public String toString()
	{
		return String.format("#%02X%02X%02X", r, g, b);
	}
}
