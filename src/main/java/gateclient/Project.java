package gateclient;

import java.nio.file.Path;

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

	public Path getBinaryPath()
	{
		return path.resolve("target").resolve("classes");
	}

	public Path getResourcePath()
	{
		return path.resolve("src").resolve("main").resolve("resources");
	}
}
