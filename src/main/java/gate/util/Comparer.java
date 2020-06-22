package gate.util;

public class Comparer
{

	public static <T extends Comparable> boolean eq(T a, T b)
	{
		return a.compareTo(b) == 0;
	}

	public static <T extends Comparable> boolean ne(T a, T b)
	{
		return a.compareTo(b) != 0;
	}

	public static <T extends Comparable> boolean lt(T a, T b)
	{
		return a.compareTo(b) < 0;
	}

	public static <T extends Comparable> boolean le(T a, T b)
	{
		return a.compareTo(b) <= 0;
	}

	public static <T extends Comparable> boolean gt(T a, T b)
	{
		return a.compareTo(b) > 0;
	}

	public static <T extends Comparable> boolean ge(T a, T b)
	{
		return a.compareTo(b) >= 0;
	}

	public static <T extends Comparable<? super T>> T max(T a, T b)
	{
		return a.compareTo(b) >= 0 ? a : b;
	}

	public static <T extends Comparable<? super T>> T min(T a, T b)
	{
		return a.compareTo(b) <= 0 ? a : b;
	}

	public static <T extends Comparable<T>> int compare(T a, T b)
	{
		if (a == b)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}
}
