package gate.code;

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

	public Path getSourcePath(PackageName packageName)
	{
		Path sourcePath = Project.this.getSourcePath();
		for (String name : packageName)
			sourcePath = sourcePath.resolve(name);
		return sourcePath;
	}

	public Path getSourceFile(ClassName className)
	{
		Path sourcePath = Project.this.getSourcePath();
		for (String name : className.getPackageName())
			sourcePath = sourcePath.resolve(name);
		return sourcePath.resolve(className.getName() + ".java");
	}

	public Path getViewPath()
	{
		return path.resolve("src").resolve("main").resolve("webapp").resolve("WEB-INF").resolve("views");
	}

	public Path getViewPath(PackageName screen, Class<?> type)
	{
		Path viewPath = getViewPath();
		for (String name : screen)
			viewPath = viewPath.resolve(name);
		return viewPath.resolve(type.getSimpleName());
	}

	public Path getResourcePath()
	{
		return path.resolve("src").resolve("main").resolve("resources");
	}

	public Path getResourcePath(PackageName packageName)
	{
		Path resourcePath = getResourcePath();
		for (String name : packageName)
			resourcePath = resourcePath.resolve(name);
		return resourcePath;
	}

	public Path getResourcePath(ClassName className)
	{
		Path resourcePath = getResourcePath();
		for (String name : className.getPackageName())
			resourcePath = resourcePath.resolve(name);
		return resourcePath.resolve(className.getName());
	}

	public Path getBinaryPath()
	{
		return path.resolve("target").resolve("classes");
	}

	public Path getBinaryPath(PackageName packageName)
	{
		Path classesPath = getBinaryPath();
		for (String name : packageName)
			classesPath = classesPath.resolve(name);
		return classesPath;
	}

	public Path getBinaryFile(ClassName className)
	{
		Path binaryPath = getBinaryPath();
		for (String name : className.getPackageName())
			binaryPath = binaryPath.resolve(name);
		return binaryPath.resolve(className.getName() + ".class");
	}

	public static Project of(Class<?> type)
	{
		return new Project(Paths.get(type.getProtectionDomain().getCodeSource()
			.getLocation().getPath()).getParent().getParent());
	}
}
