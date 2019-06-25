package gate.code;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackageName implements Iterable<String>
{

	private final String[] names;

	private PackageName(String[] names)
	{
		this.names = names;
	}

	public PackageName getParent()
	{
		return new PackageName(Stream.of(names).limit(names.length - 1).toArray(String[]::new));
	}

	public PackageName resolve(String name)
	{
		return new PackageName(Stream.concat(Stream.of(names), Stream.of(name)).toArray(String[]::new));
	}

	public PackageName resolveSibling(String name)
	{
		return getParent().resolve(name);
	}

	public static PackageName of(Class<?> type)
	{
		return of(type.getPackageName());
	}

	public static PackageName of(String string)
	{
		return of(string.split("[.]"));
	}

	public static PackageName of(String[] names)
	{
		return new PackageName(names);
	}

	public String getFolderName()
	{
		return stream().collect(Collectors.joining("/"));
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
