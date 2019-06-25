package gate.code;

import java.util.Iterator;
import java.util.stream.Stream;

public class ClassName implements Iterable<String>
{

	private final PackageName packageName;
	private final String name;

	private ClassName(PackageName packageName, String name)
	{
		this.packageName = packageName;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public PackageName getPackageName()
	{
		return packageName;
	}

	public String getFolderName()
	{
		return packageName.getFolderName() + "/" + name;
	}

	public static ClassName of(Class<?> type)
	{
		return of(PackageName.of(type), type.getSimpleName());
	}

	public static ClassName of(String string)
	{
		String[] names = string.split("[.]");
		return of(PackageName.of(Stream.of(names).limit(names.length - 1).toArray(String[]::new)),
			names[names.length - 1]);
	}

	public static ClassName of(PackageName pack, String string)
	{
		return new ClassName(pack, string);
	}

	@Override
	public String toString()
	{
		return packageName.toString() + "." + name;
	}

	public Stream<String> stream()
	{
		return Stream.concat(packageName.stream(), Stream.of(name));
	}

	@Override
	public Iterator<String> iterator()
	{
		return stream().iterator();
	}
}
