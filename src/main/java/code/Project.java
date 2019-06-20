package code;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Project
{

	private final Path path;

	public Project(Path path)
	{
		this.path = path;
	}

	public Path getPath()
	{
		return path;
	}

	public Path getSourcePath()
	{
		return path.resolve("src").resolve("main").resolve("java");
	}

	public Path getClassesPath()
	{
		return path.resolve("target").resolve("classes");
	}

	public Path getResourcePath()
	{
		return path.resolve("src").resolve("main").resolve("resources");
	}

	public Path getSourcesPath(Pack pack)
	{
		Path sourcePath = getSourcePath();
		for (String name : pack)
			sourcePath = sourcePath.resolve(name);
		return sourcePath;
	}

	public Path getClassesPath(Pack pack)
	{
		Path classesPath = getClassesPath();
		for (String name : pack)
			classesPath = classesPath.resolve(name);
		return classesPath;
	}

	public Path getResourcePath(Pack pack)
	{
		Path resourcePath = getResourcePath();
		for (String name : pack)
			resourcePath = resourcePath.resolve(name);
		return resourcePath;
	}

	public Path getSourcesPath(JavaSourceFile javaSourceFile)
	{
		Path sourcePath = getSourcePath();
		for (String name : javaSourceFile)
			sourcePath = sourcePath.resolve(name);
		return sourcePath;
	}

	public Path getClassesPath(JavaSourceFile javaSourceFile)
	{
		Path classesPath = getClassesPath();
		for (String name : javaSourceFile)
			classesPath = classesPath.resolve(name);
		return classesPath;
	}

	public Path getResourcePath(JavaSourceFile javaSourceFile)
	{
		Path resourcePath = getResourcePath();
		for (String name : javaSourceFile)
			resourcePath = resourcePath.resolve(name);
		return resourcePath;
	}

	public static Project of(Class<?> type)
	{
		return new Project(Paths.get(type.getProtectionDomain().getCodeSource()
			.getLocation().getPath()).getParent().getParent());
	}
}
