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
@ApplicationScoped
public class VersionProducer
{

	private Version version;

	@Inject
	private ServletContext servletContext;

	@Produces
	@Named(value = "version")
	public Version produce() throws IOException
	{
		if (version == null)
			try (InputStream inputStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"))
			{
				if (inputStream != null)
				{
					Manifest manifest = new Manifest(inputStream);
					version = Version.of(manifest.getMainAttributes().getValue("Implementation-Version"));
				} else
					version = Version.of("0.0.0-SNAPSHOT");
			} catch (ParseException ex)
			{
				throw new IOException(ex.getMessage(), ex);
			}
		return version;
	}

}
