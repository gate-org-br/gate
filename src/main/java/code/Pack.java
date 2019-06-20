package code;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pack implements Iterable<String>
{

	private final String[] names;

	private Pack(String[] names)
	{
		this.names = names;
	}

	public Pack getParent()
	{
		return new Pack(Stream.of(names).limit(names.length - 1).toArray(String[]::new));
	}

	public Pack resolve(String name)
	{
		return new Pack(Stream.concat(Stream.of(names), Stream.of(name)).toArray(String[]::new));
	}

	public Pack resolveSibling(String name)
	{
		return getParent().resolve(name);
	}

	public static Pack of(Class<?> type)
	{
		return of(type.getPackageName());
	}

	public static Pack of(String string)
	{
		return of(string.split("[.]"));
	}

	public static Pack of(String[] names)
	{
		return new Pack(names);
	}

	public int size()
	{
		return names.length;
	}

	@Override
	public Iterator<String> iterator()
	{
		return stream().iterator();
	}

	public Stream<String> stream()
	{
		return Arrays.stream(names);
	}

	@Override
	public String toString()
	{
		return Stream.of(names).collect(Collectors.joining("."));
	}
}
