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
	
	public Path getSourcePath(PackageName pack)
	{
		Path sourcePath = Project.this.getSourcePath();
		for (String name : pack)
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
	
	public Path getBinaryPath(PackageName pack)
	{
		Path classesPath = getClassesPath();
		for (String name : pack)
			classesPath = classesPath.resolve(name);
		return classesPath;
	}
	
	public Path getResourcePath(PackageName pack)
	{
		Path resourcePath = getResourcePath();
		for (String name : pack)
			resourcePath = resourcePath.resolve(name);
		return resourcePath;
	}
	
	public Path getSourcesPath(ClassName javaSourceFile)
	{
		Path sourcePath = Project.this.getSourcePath();
		for (String name : javaSourceFile)
			sourcePath = sourcePath.resolve(name);
		return sourcePath;
	}
	
	public Path getClassesPath(ClassName javaSourceFile)
	{
		Path classesPath = getClassesPath();
		for (String name : javaSourceFile)
			classesPath = classesPath.resolve(name);
		return classesPath;
	}
	
	public Path getResourcePath(ClassName javaSourceFile)
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
