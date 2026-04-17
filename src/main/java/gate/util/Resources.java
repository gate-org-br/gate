package gate.util;

import java.io.*;

public class Resources
{

	public static String getTextResource(java.net.URL resource)
	{
		try
		{
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream())))
			{
				try (StringWriter writer = new StringWriter())
				{
					for (int c = reader.read();
					     c != -1; c = reader.read())
						writer.write((char) c);
					writer.flush();
					return writer.toString();
				}
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}