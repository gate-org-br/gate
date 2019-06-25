package gate.code;

import gate.error.AppException;
import gate.util.ConsoleParameters;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Stream;

public class Code
{

	private static final ConsoleParameters.Flag ENTITY = ConsoleParameters.Flag.builder().shortcut("-e").longname("--entity")
		.description("Define the list of entities").build();

	private static final ConsoleParameters.Flag CLASSPATH = ConsoleParameters.Flag.builder().shortcut("-cp").longname("--classpath")
		.description("Specify the classpath of the specified entities").build();

	private static final ConsoleParameters.Flag GENERATE_DAO = ConsoleParameters.Flag.builder().shortcut("-d").longname("--dao")
		.defaultValue(":generate").description("Generate the Dao java file of each specified entity").build();

	private static final ConsoleParameters.Flag GENERATE_CONTROL = ConsoleParameters.Flag.builder().shortcut("-c").longname("--control")
		.defaultValue(":generate").description("Generate the Control java file of each specified entity").build();

	public static void main(String[] args) throws AppException, ClassNotFoundException, Exception
	{
		ConsoleParameters consoleParameters = ConsoleParameters.parse(args, ENTITY, CLASSPATH, GENERATE_DAO, GENERATE_CONTROL);

		URL[] classpath = Stream.of(consoleParameters.get(CLASSPATH).orElseThrow(() -> new AppException("No classpath specified")).split(":")).map(e
			->
		{
			try
			{
				return new File(e).toURI().toURL();
			} catch (MalformedURLException ex)
			{
				System.out.println("Invalid URL specified: " + e);
				System.exit(1);
				return null;
			}
		}).toArray(URL[]::new);

		String entity = consoleParameters.get(ENTITY).orElseThrow(() -> new AppException("No java entity specified"));
		ClassLoader classLoader = URLClassLoader.newInstance(classpath, Code.class.getClassLoader());
		Class<?> type = Class.forName(entity, true, classLoader);

		if (consoleParameters.get(GENERATE_DAO).isPresent())
		{
			createDao(type);
		}

		if (consoleParameters.get(GENERATE_CONTROL).isPresent())
		{
			createControl(type);
		}
	}

	public static void createDao(Class<?> type) throws URISyntaxException
	{
		
	}

	public static void createControl(Class<?> type) throws URISyntaxException
	{
	}
}
