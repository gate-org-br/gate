package gate.util;

public class Comparer
{

	public static <T extends Comparable<? super T>> boolean eq(T a, T b)
	{
		return a.compareTo(b) == 0;
	}

	public static <T extends Comparable<? super T>> boolean ne(T a, T b)
	{
		return a.compareTo(b) != 0;
	}

	public static <T extends Comparable<? super T>> boolean lt(T a, T b)
	{
		return a.compareTo(b) < 0;
	}

	public static <T extends Comparable<? super T>> boolean le(T a, T b)
	{
		return a.compareTo(b) <= 0;
	}

	public static <T extends Comparable<? super T>> boolean gt(T a, T b)
	{
		return a.compareTo(b) > 0;
	}

	public static <T extends Comparable<? super T>> boolean ge(T a, T b)
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
