package gate.producer;

import gate.type.Version;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

/**
 * Produces a Version object with the version number of the current application.
 */
@ApplicationScoped
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
		return Version.valueOf(version != null ? version : manifest());
	}

	private String manifest() throws IOException
	{
		try (InputStream inputStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"))
		{
			return inputStream != null
					? new Manifest(inputStream).getMainAttributes().getValue("Implementation-Version")
					: null;
		}
	}
}