package gate.producer;

import gate.type.Version;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Produces a Version object with the version number of the current application.
 */
public class VersionProducer
{

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	@Inject
	private ServletContext servletContext;

	@Produces
	@ApplicationScoped
	@Named(value = "version")
	public Version produce() throws IOException
	{
		return Version.of(version != null ? version : manifest());
	}

	private String manifest() throws IOException
	{
		try ( InputStream inputStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"))
		{
			return inputStream != null
				? new Manifest(inputStream).getMainAttributes().getValue("Implementation-Version")
				: null;
		}
	}
}
