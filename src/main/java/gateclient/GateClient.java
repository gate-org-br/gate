package gateclient;

import gate.error.AppError;
import gate.error.AppException;
import gate.util.ConsoleParameters;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GateClient
{

	public static void main(String[] args) throws AppException, ClassNotFoundException, Exception
	{
		ConsoleParameters consoleParameters = ConsoleParameters.parse(args);

		URL[] classpath = consoleParameters
			.get(Flag.CLASSPATH.getShortcut(),
				Flag.CLASSPATH.getLongname())
			.stream()
			.flatMap(e -> e.stream()).flatMap(e -> Stream.of(e.split(":")))
			.map(e ->
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

		ClassLoader classLoader = URLClassLoader.newInstance(classpath, GateClient.class.getClassLoader());

		List<Class<?>> types = consoleParameters.get(Flag.ENTITY.getShortcut(),
			Flag.ENTITY.getLongname()).orElseThrow(() -> new AppException("No java entity specified"))
			.stream().map(e ->
			{
				try
				{
					return Class.forName(e, true, classLoader);
				} catch (ClassNotFoundException ex)
				{
					System.out.println("Invalid class name specified: " + e);
					System.exit(1);
					return null;
				}

			}).collect(Collectors.toList());

		if (consoleParameters.get(Flag.GENERATE_DAO.getShortcut(), Flag.GENERATE_DAO.getLongname()).isPresent())
			for (Class<?> type : types)
				createDao(type);

		if (consoleParameters.get(Flag.GENERATE_CONTROL.getShortcut(), Flag.GENERATE_CONTROL.getLongname()).isPresent())
			for (Class<?> type : types)
				createControl(type);
	}

	public static void createDao(Class<?> type) throws URISyntaxException
	{
		new DaoGenerator(type).dao(Pack.of(type)
			.resolveSibling("dao")
			.getSourcesPath()
			.resolve(type.getSimpleName() + "Dao.java")
			.toFile());
	}

	public static void createControl(Class<?> type) throws URISyntaxException
	{
		new ControlGenerator(type).control(Pack.of(type)
			.resolveSibling("control")
			.getSourcesPath()
			.resolve(type.getSimpleName() + "Control.java")
			.toFile());
	}
}
