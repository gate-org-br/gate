package code;

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
		//   args = "-cp=/home/davins/Projetos/gate/target/classes:/home/davins/.m2/repository/org/reflections/reflections/0.9.11/reflections-0.9.11.jar:/home/davins/.m2/repository/com/google/guava/guava/20.0/guava-20.0.jar:/home/davins/.m2/repository/org/javassist/javassist/3.21.0-GA/javassist-3.21.0-GA.jar:/home/davins/.m2/repository/com/jcraft/jsch/0.1.54/jsch-0.1.54.jar:/home/davins/.m2/repository/javax/mail/javax.mail-api/1.5.6/javax.mail-api-1.5.6.jar:/home/davins/.m2/repository/commons-net/commons-net/3.5/commons-net-3.5.jar:/home/davins/.m2/repository/org/apache/poi/poi/4.1.0/poi-4.1.0.jar:/home/davins/.m2/repository/commons-codec/commons-codec/1.12/commons-codec-1.12.jar:/home/davins/.m2/repository/org/apache/commons/commons-collections4/4.3/commons-collections4-4.3.jar:/home/davins/.m2/repository/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar:/home/davins/.m2/repository/org/apache/poi/poi-ooxml/4.1.0/poi-ooxml-4.1.0.jar:/home/davins/.m2/repository/org/apache/poi/poi-ooxml-schemas/4.1.0/poi-ooxml-schemas-4.1.0.jar:/home/davins/.m2/repository/org/apache/xmlbeans/xmlbeans/3.1.0/xmlbeans-3.1.0.jar:/home/davins/.m2/repository/org/apache/commons/commons-compress/1.18/commons-compress-1.18.jar:/home/davins/.m2/repository/com/github/virtuald/curvesapi/1.06/curvesapi-1.06.jar:/home/davins/.m2/repository/com/itextpdf/itextpdf/5.5.12/itextpdf-5.5.12.jar:/home/davins/.m2/repository/ch/ethz/ganymed/ganymed-ssh2/262/ganymed-ssh2-262.jar:/home/davins/.m2/repository/javax/jms/javax.jms-api/2.0.1/javax.jms-api-2.0.1.jar:/home/davins/.m2/repository/io/jsonwebtoken/jjwt-api/0.10.5/jjwt-api-0.10.5.jar:/home/davins/.m2/repository/io/jsonwebtoken/jjwt-impl/0.10.5/jjwt-impl-0.10.5.jar:/home/davins/.m2/repository/io/jsonwebtoken/jjwt-jackson/0.10.5/jjwt-jackson-0.10.5.jar:/home/davins/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.9.6/jackson-databind-2.9.6.jar:/home/davins/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.9.0/jackson-annotations-2.9.0.jar:/home/davins/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.9.6/jackson-core-2.9.6.jar -e=gate.entity.User -d".split(" ");

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
		new DaoGenerator(type).dao(Project.of(type).getSourcesPath(Pack.of(type).resolveSibling("dao"))
			.resolve(type.getSimpleName() + "Dao.java")
			.toFile());
	}

	public static void createControl(Class<?> type) throws URISyntaxException
	{
		new ControlGenerator(type).control(Project.of(type).getSourcesPath(Pack.of(type).resolveSibling("control"))
			.resolve(type.getSimpleName() + "Control.java")
			.toFile());
	}
}
