package gateclient;

import gate.entity.Func;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pack implements Iterable<String>
{

	private final Project project;
	private final String[] names;

	private Pack(Project project, String[] names)
	{
		this.project = project;
		this.names = names;
	}

	public Pack getParent()
	{
		return new Pack(project, Stream.of(names).limit(names.length - 1).toArray(String[]::new));
	}

	public Pack resolve(String name)
	{
		return new Pack(project, Stream.concat(Stream.of(names), Stream.of(name)).toArray(String[]::new));
	}

	public Pack resolveSibling(String name)
	{
		return getParent().resolve(name);
	}

	public Path getSourcesPath()
	{
		Path path = getProject().getSourcePath();
		for (String name : names)
			path = path.resolve(name);
		return path;
	}

	public Path getClassesPath()
	{
		Path path = getProject().getBinaryPath();
		for (String name : names)
			path = path.resolve(name);
		return path;
	}

	public Path getResourcePath()
	{
		Path path = getProject().getResourcePath();
		for (String name : names)
			path = path.resolve(name);
		return path;
	}

	public Project getProject()
	{
		return project;
	}

	public static Pack of(Class<?> type) throws URISyntaxException
	{
		String[] names = type.getPackageName().split("[.]");

		Path project = Paths.get(type.getProtectionDomain().getCodeSource()
			.getLocation().getPath()).getParent().getParent();

		return new Pack(new Project(project), names);
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
