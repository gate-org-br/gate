package gate.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Stream;

public class ClasspathLoader
{

	public static Class<?> forName(ClassLoader classloader, String classpath, String classname) throws ClassNotFoundException
	{
		URL[] urls = Stream.of(classpath.split(":")).map(e -> ClasspathLoader.url(e)).toArray(URL[]::new);
		classloader = URLClassLoader.newInstance(urls, classloader);
		return Class.forName(classname, true, classloader);
	}

	private static URL url(String string)
	{
		try
		{
			return new File(string).toURI().toURL();
		} catch (MalformedURLException ex)
		{
			throw new IllegalArgumentException(string, ex);
		}
	}
}
