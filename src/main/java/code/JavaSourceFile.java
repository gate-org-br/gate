package code;

import java.util.Iterator;
import java.util.stream.Stream;

public class JavaSourceFile implements Iterable<String>
{

	private final Pack pack;
	private final String name;

	private JavaSourceFile(Pack pack, String name)
	{
		this.pack = pack;
		this.name = name;
	}

	public static JavaSourceFile of(Class<?> type)
	{
		return of(Pack.of(type), type.getSimpleName() + ".java");
	}

	public static JavaSourceFile of(String string)
	{
		String[] names = string.split("[.]");
		return of(Pack.of(Stream.of(names).limit(names.length - 1).toArray(String[]::new)),
			names[names.length - 1]);
	}

	public static JavaSourceFile of(Pack pack, String string)
	{
		return new JavaSourceFile(pack, string);
	}

	@Override
	public String toString()
	{
		return pack.toString() + "." + name;
	}

	public Stream<String> stream()
	{
		return Stream.concat(pack.stream(), Stream.of(name));
	}

	@Override
	public Iterator<String> iterator()
	{
		return stream().iterator();
	}
}
