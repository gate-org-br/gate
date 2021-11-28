package gate.producer;

import gate.type.Version;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.jar.Manifest;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

/**
 * Produces a Version object with the version number of the current application.
 */
public class VersionProducer
{

	@Inject
	private ServletContext servletContext;

	@Produces
	@ApplicationScoped
	@Named(value = "version")
	public Version produce() throws IOException
	{
		try ( InputStream inputStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"))
		{
			if (inputStream == null)
				return Version.UNDEFINED;

			Manifest manifest = new Manifest(inputStream);
			String value = manifest.getMainAttributes().getValue("Implementation-Version");
			return value != null ? Version.of(value) : Version.UNDEFINED;
		} catch (ParseException ex)
		{
			return Version.INVALID;
		}
	}
}
